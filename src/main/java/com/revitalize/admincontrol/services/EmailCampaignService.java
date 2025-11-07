package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.dto.EmailAttachmentDto;
import com.revitalize.admincontrol.dto.EmailPersonalizationDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailJobModel;
import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.models.enums.EmailJobStatus;
import com.revitalize.admincontrol.repository.EmailJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;
import java.text.Normalizer;
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

    private final EmailJobRepository emailJobRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailCampaignService.class);

    public EmailCampaignService(EmailJobRepository emailJobRepository) {
        this.emailJobRepository = emailJobRepository;
    }

    public List<EmailJobModel> findByUsuario(UUID usuarioId) {
        return emailJobRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public EmailJobModel registerJob(AdmUsuarioModel usuario,
                                     EmailTemplateModel template,
                                     String subject,
                                     String preview,
                                     List<String> recipients) {
        EmailJobModel job = new EmailJobModel();
        job.setUsuario(usuario);
        job.setTemplate(template);
        job.setAssunto(subject);
        job.setMensagemPreview(preview);
        job.setDestinatarios(String.join(",", recipients));
        job.setStatus(EmailJobStatus.PENDENTE);
        return emailJobRepository.save(job);
    }

    @Transactional
    public void updateJobStatus(EmailJobModel job, EmailJobStatus status) {
        job.setStatus(status);
        emailJobRepository.save(job);
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
                helper.setText(contentToSend, true);
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
}
