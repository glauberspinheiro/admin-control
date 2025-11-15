package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.EmailAttachmentDto;
import com.revitalize.admincontrol.dto.EmailSendRequestDto;
import com.revitalize.admincontrol.dto.EmailPersonalizationDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailJobModel;
import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.models.enums.EmailJobStatus;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.repository.EmailJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.util.ByteArrayDataSource;
import java.text.Normalizer;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EmailCampaignService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("@([A-Z0-9_]+)");
    private static final Pattern INLINE_IMAGE_PATTERN = Pattern.compile(
            "<img\\s+[^>]*src=(['\"])(data:(image/[^;\"']+);base64,([^'\"<>]+))\\1[^>]*>",
            Pattern.CASE_INSENSITIVE);

    private final EmailJobRepository emailJobRepository;
    private final EmailServerConfigService emailServerConfigService;
    private final EmailTemplateService emailTemplateService;
    private static final Logger logger = LoggerFactory.getLogger(EmailCampaignService.class);

    public EmailCampaignService(EmailJobRepository emailJobRepository,
                                EmailServerConfigService emailServerConfigService,
                                EmailTemplateService emailTemplateService) {
        this.emailJobRepository = emailJobRepository;
        this.emailServerConfigService = emailServerConfigService;
        this.emailTemplateService = emailTemplateService;
    }

    public List<EmailJobModel> findByUsuario(UUID usuarioId) {
        return emailJobRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public EmailJobModel registerJob(AdmUsuarioModel usuario,
                                     EmailTemplateModel template,
                                     String subject,
                                     String preview,
                                     List<String> recipients, EmailJobStatus status) {
        EmailJobModel job = new EmailJobModel();
        job.setUsuario(usuario);
        job.setTemplate(template);
        job.setAssunto(subject);
        job.setMensagemPreview(preview);
        job.setDestinatarios(String.join(",", recipients));
        job.setStatus(status);
        return emailJobRepository.save(job);
    }

    @Transactional
    public void updateJobStatusInNewTransaction(UUID jobId, EmailJobStatus status) {
        emailJobRepository.findById(jobId).ifPresent(job -> {
            job.setStatus(status);
            emailJobRepository.save(job);
        });
    }

    @Async
    public void processCampaign(UUID jobId, EmailSendRequestDto dto, EmailServerConfigModel config) {
        try {
            boolean incluirAssinatura = dto.getIncluirAssinatura() == null || dto.getIncluirAssinatura();

            sendEmails(
                    config,
                    dto.getAssunto(),
                    dto.getConteudoHtml(),
                    dto.getDestinatarios(),
                    dto.getPersonalizacoes(),
                    dto.getAnexos(),
                    incluirAssinatura);

            updateJobStatusInNewTransaction(jobId, EmailJobStatus.ENVIADO);

        } catch (Exception ex) {
            logger.error("Falha ao processar campanha de e-mail {}", jobId, ex);
            updateJobStatusInNewTransaction(jobId, EmailJobStatus.FALHOU);
        }
    }

    @Transactional
    public EmailJobModel scheduleCampaign(EmailSendRequestDto dto, AdmUsuarioModel usuario) {
        EmailServerConfigModel config = emailServerConfigService.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new EntityNotFoundException("Configuração de servidor de e-mail não encontrada para o usuário"));

        EmailTemplateModel template = null;
        if (dto.getTemplateId() != null) {
            template = emailTemplateService.findById(dto.getTemplateId())
                    .orElseThrow(() -> new EntityNotFoundException("Template informado não encontrado"));
        }

        String preview = dto.getConteudoHtml() == null ? "" : dto.getConteudoHtml().replaceAll("<[^>]*>", "").trim();
        if (preview.length() > 120) {
            preview = preview.substring(0, 120);
        }

        EmailJobModel job = registerJob(usuario, template, dto.getAssunto(), preview, dto.getDestinatarios(), EmailJobStatus.PROCESSANDO);
        processCampaign(job.getId(), dto, config);
        return job;
    }

    public void sendEmails(EmailServerConfigModel config,
                           String subject,
                           String htmlContent,
                           List<String> recipients,
                           List<EmailPersonalizationDto> personalizations,
                           List<EmailAttachmentDto> attachments,
                           boolean includeSignature) {
        boolean implicitSsl = isImplicitSslPort(config.getSmtpPort());
        boolean useSsl = config.isUseSsl() || implicitSsl;
        boolean startTls = !useSsl && config.isUseStartTls();

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getSmtpHost());
        sender.setPort(config.getSmtpPort());
        sender.setUsername(config.getSmtpUsername());
        sender.setPassword(config.getSmtpPassword());
        String protocol = resolveProtocol(useSsl, config.getSmtpProtocol());
        sender.setProtocol(protocol);
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.starttls.required", String.valueOf(startTls));
        props.put("mail.smtp.ssl.enable", String.valueOf(useSsl));
        props.put("mail.smtp.ssl.trust", config.getSmtpHost());
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        if (useSsl) {
            props.put("mail.smtp.socketFactory.port", String.valueOf(config.getSmtpPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }

        Map<String, Map<String, String>> personalizationMap = buildPersonalizationMap(personalizations);

        if (logger.isInfoEnabled()) {
            logger.info("Iniciando envio de e-mails via SMTP host {} porta {} SSL={} STARTTLS={} protocolo={}",
                    config.getSmtpHost(), config.getSmtpPort(), useSsl, startTls, protocol);
        }

        for (String recipient : recipients.stream().map(rec -> rec == null ? "" : rec.trim()).collect(Collectors.toList())) {
            if (recipient.isEmpty()) {
                continue;
            }
            try {
                var message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(config.getSmtpUsername());
                helper.setTo(recipient);
                helper.setSubject(subject);
                Map<String, String> vars = personalizationMap.get(recipient.toLowerCase());
                String contentToSend = applyPlaceholders(htmlContent, vars);
                contentToSend = appendSignatureIfNeeded(contentToSend, includeSignature, config.getSignatureHtml());
                InlineImageProcessingResult inlineResult = processInlineImages(contentToSend);
                helper.setText(inlineResult.getHtml(), true);
                attachInlineImages(helper, inlineResult.getInlineImages());
                attachFiles(helper, attachments);
                sender.send(message);
            } catch (Exception e) {
                logger.error("Erro ao enviar e-mail para {} usando host {}:{} - {}", recipient, config.getSmtpHost(), config.getSmtpPort(), e.getMessage(), e);
                throw new RuntimeException("Falha ao enviar e-mail para " + recipient + ": " + e.getMessage(), e);
            }
        }

        logger.info("Envio concluído: {} destinatário(s) processados.", recipients.size());
    }

    private Map<String, Map<String, String>> buildPersonalizationMap(List<EmailPersonalizationDto> personalizations) {
        if (personalizations == null || personalizations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, String>> result = new HashMap<>();
        for (EmailPersonalizationDto personalization : personalizations) {
            if (personalization == null || personalization.getDestinatario() == null) {
                continue;
            }
            String emailKey = personalization.getDestinatario().trim().toLowerCase();
            if (emailKey.isEmpty() || result.containsKey(emailKey)) {
                continue;
            }
            result.put(emailKey, normalizeVariables(personalization.getVariaveis()));
        }
        return result;
    }

    private Map<String, String> normalizeVariables(Map<String, String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> normalized = new HashMap<>();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String key = normalizePlaceholderKey(entry.getKey());
            if (!key.isEmpty()) {
                normalized.put(key, entry.getValue() == null ? "" : entry.getValue());
            }
        }
        return normalized;
    }

    private String normalizePlaceholderKey(String rawKey) {
        if (rawKey == null) {
            return "";
        }
        String normalized = Normalizer.normalize(rawKey, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^A-Za-z0-9]+", "_")
                .replaceAll("^_+|_+$", "")
                .toUpperCase();
        return normalized;
    }

    private String applyPlaceholders(String htmlContent, Map<String, String> variables) {
        if (htmlContent == null || variables == null || variables.isEmpty()) {
            return htmlContent;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(htmlContent);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String token = matcher.group(1).toUpperCase();
            String fallback = matcher.group(0);
            String replacement = variables.getOrDefault(token, fallback);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String appendSignatureIfNeeded(String html, boolean includeSignature, String signatureHtml) {
        if (!includeSignature || signatureHtml == null || signatureHtml.isBlank()) {
            return html;
        }
        StringBuilder builder = new StringBuilder(html == null ? "" : html);
        builder.append("<br/><br/>").append(signatureHtml);
        return builder.toString();
    }

    private void attachFiles(MimeMessageHelper helper, List<EmailAttachmentDto> attachments) throws Exception {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for (EmailAttachmentDto attachment : attachments) {
            if (attachment == null || attachment.getConteudoBase64() == null || attachment.getConteudoBase64().isBlank()) {
                continue;
            }
            String filename = attachment.getNomeArquivo();
            if (filename == null || filename.isBlank()) {
                filename = "anexo";
            }
            String contentType = (attachment.getContentType() == null || attachment.getContentType().isBlank())
                    ? "application/octet-stream"
                    : attachment.getContentType();
            byte[] data = Base64.getDecoder().decode(attachment.getConteudoBase64());
            helper.addAttachment(filename, new ByteArrayDataSource(data, contentType));
        }
    }

    private void attachInlineImages(MimeMessageHelper helper, List<InlineImageAttachment> inlineImages) throws Exception {
        if (inlineImages == null || inlineImages.isEmpty()) {
            return;
        }
        for (InlineImageAttachment inlineImage : inlineImages) {
            helper.addInline(inlineImage.getCid(), new ByteArrayDataSource(inlineImage.getData(), inlineImage.getContentType()));
        }
    }

    private String resolveProtocol(boolean useSsl, String configuredProtocol) {
        if (useSsl) {
            return "smtps";
        }
        String configured = configuredProtocol;
        if (configured == null || configured.isBlank()) {
            return "smtp";
        }
        return configured.toLowerCase().startsWith("smtp") ? configured : "smtp";
    }

    private boolean isImplicitSslPort(Integer port) {
        if (port == null) {
            return false;
        }
        return port == 465;
    }

    private InlineImageProcessingResult processInlineImages(String html) {
        if (html == null || html.isBlank()) {
            return new InlineImageProcessingResult(html, Collections.emptyList());
        }
        Matcher matcher = INLINE_IMAGE_PATTERN.matcher(html);
        StringBuffer buffer = new StringBuffer();
        List<InlineImageAttachment> inlineImages = new ArrayList<>();
        boolean replaced = false;
        while (matcher.find()) {
            String dataUri = matcher.group(2);
            String contentType = matcher.group(3);
            String base64Data = matcher.group(4);
            String cid = "inline-" + UUID.randomUUID();
            byte[] data;
            try {
                data = Base64.getDecoder().decode(base64Data.replaceAll("\\s+", ""));
            } catch (IllegalArgumentException ex) {
                logger.warn("Imagem inline ignorada: payload inválido ({})", ex.getMessage());
                matcher.appendReplacement(buffer, matcher.group(0));
                continue;
            }
            inlineImages.add(new InlineImageAttachment(
                    cid,
                    (contentType == null || contentType.isBlank()) ? "image/png" : contentType,
                    data
            ));
            String replacementTag = matcher.group(0).replace(dataUri, "cid:" + cid);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacementTag));
            replaced = true;
        }
        matcher.appendTail(buffer);
        return new InlineImageProcessingResult(
                replaced ? buffer.toString() : html,
                inlineImages
        );
    }

    private static class InlineImageAttachment {
        private final String cid;
        private final String contentType;
        private final byte[] data;

        InlineImageAttachment(String cid, String contentType, byte[] data) {
            this.cid = cid;
            this.contentType = contentType;
            this.data = data;
        }

        public String getCid() {
            return cid;
        }

        public String getContentType() {
            return contentType;
        }

        public byte[] getData() {
            return data;
        }
    }

    private static class InlineImageProcessingResult {
        private final String html;
        private final List<InlineImageAttachment> inlineImages;

        InlineImageProcessingResult(String html, List<InlineImageAttachment> inlineImages) {
            this.html = html;
            this.inlineImages = inlineImages;
        }

        public String getHtml() {
            return html;
        }

        public List<InlineImageAttachment> getInlineImages() {
            return inlineImages;
        }
    }
}
