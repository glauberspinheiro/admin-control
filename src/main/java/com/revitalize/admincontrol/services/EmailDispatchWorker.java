package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.condicionante.EmailDispatchTaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailDispatchWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailDispatchWorker.class);

    private final EmailDispatchQueueService queueService;
    private final NotificationEmailService emailService;

    public EmailDispatchWorker(EmailDispatchQueueService queueService,
                               NotificationEmailService emailService) {
        this.queueService = queueService;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelayString = "${condicionantes.queue.worker-interval:PT15S}")
    public void processarFila() {
        List<EmailDispatchTaskModel> tasks = queueService.claimNextBatch();
        if (tasks.isEmpty()) {
            return;
        }
        LOGGER.info("Processando {} tarefas de e-mail de condicionantes", tasks.size());
        for (EmailDispatchTaskModel task : tasks) {
            try {
                emailService.enviarEmail(task.getAssunto(), task.getCorpo(), parseDestinatarios(task.getDestinatarios()));
                queueService.markAsSent(task);
            } catch (Exception ex) {
                LOGGER.error("Falha ao enviar e-mail da tarefa {}: {}", task.getId(), ex.getMessage());
                queueService.markAsFailed(task, ex.getMessage());
            }
        }
    }

    private List<String> parseDestinatarios(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
