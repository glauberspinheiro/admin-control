package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.config.CondicionanteProperties;
import com.revitalize.admincontrol.models.condicionante.CondicionanteAlertaModel;
import com.revitalize.admincontrol.models.condicionante.EmailDispatchTaskModel;
import com.revitalize.admincontrol.models.enums.EmailDispatchStatus;
import com.revitalize.admincontrol.repository.EmailDispatchTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EmailDispatchQueueService {

    private final EmailDispatchTaskRepository repository;
    private final CondicionanteProperties properties;

    public EmailDispatchQueueService(EmailDispatchTaskRepository repository,
                                     CondicionanteProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @Transactional
    public EmailDispatchTaskModel enqueue(CondicionanteAlertaModel alerta,
                                          String assunto,
                                          String corpo,
                                          List<String> destinatarios,
                                          OffsetDateTime scheduledFor) {
        EmailDispatchTaskModel task = new EmailDispatchTaskModel();
        task.setAlerta(alerta);
        task.setCondicionante(alerta.getCondicionante());
        task.setAssunto(assunto);
        task.setCorpo(corpo);
        task.setDestinatarios(String.join(",", destinatarios == null ? Collections.emptyList() : destinatarios));
        task.setScheduledFor(scheduledFor);
        task.setMaxTentativas(properties.getQueue().getMaxAttempts());
        return repository.save(task);
    }

    @Transactional
    public List<EmailDispatchTaskModel> claimNextBatch() {
        List<EmailDispatchTaskModel> tasks = repository
                .findTop50ByStatusAndScheduledForLessThanEqualOrderByScheduledForAsc(
                        EmailDispatchStatus.PENDENTE,
                        OffsetDateTime.now());
        OffsetDateTime now = OffsetDateTime.now();
        tasks.forEach(task -> {
            task.setStatus(EmailDispatchStatus.PROCESSANDO);
            task.setLockedAt(now);
        });
        return tasks;
    }

    @Transactional
    public void markAsSent(EmailDispatchTaskModel task) {
        task.setStatus(EmailDispatchStatus.ENVIADO);
        task.setProcessedAt(OffsetDateTime.now());
        task.setLockedAt(null);
        repository.save(task);
    }

    @Transactional
    public void markAsFailed(EmailDispatchTaskModel task, String error) {
        task.setTentativas(task.getTentativas() + 1);
        task.setLastError(error);
        task.setLockedAt(null);
        if (task.getTentativas() >= task.getMaxTentativas()) {
            task.setStatus(EmailDispatchStatus.FALHOU);
            task.setProcessedAt(OffsetDateTime.now());
        } else {
            task.setStatus(EmailDispatchStatus.PENDENTE);
            task.setScheduledFor(OffsetDateTime.now().plusMinutes(5));
        }
        repository.save(task);
    }

    public long filaPendente() {
        return repository.countByStatus(EmailDispatchStatus.PENDENTE);
    }
}
