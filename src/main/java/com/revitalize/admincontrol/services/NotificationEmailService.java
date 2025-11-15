package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.config.NotificationEmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class NotificationEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEmailService.class);

    private final JavaMailSender mailSender;
    private final NotificationEmailProperties properties;

    public NotificationEmailService(JavaMailSender mailSender, NotificationEmailProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public void enviarEmail(String subject, String bodyHtml, List<String> destinatarios) {
        if (destinatarios == null || destinatarios.isEmpty()) {
            LOGGER.warn("Tentativa de disparo de alerta sem destinatários. Assunto {}", subject);
            return;
        }
        try {
            var message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_NO, "UTF-8");
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);
            helper.setFrom(new InternetAddress(properties.getFrom(), properties.getDisplayName(), StandardCharsets.UTF_8.name()));
            helper.setTo(destinatarios.toArray(new String[0]));
            mailSender.send(message);
        } catch (Exception ex) {
            LOGGER.error("Falha ao enviar alerta por e-mail: {}", ex.getMessage(), ex);
            throw new IllegalStateException("Não foi possível enviar o e-mail de alerta", ex);
        }
    }
}
