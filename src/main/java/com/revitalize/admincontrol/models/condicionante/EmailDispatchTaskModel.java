package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.enums.EmailDispatchStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_EMAIL_DISPATCH_TASK")
public class EmailDispatchTaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alerta_id")
    private CondicionanteAlertaModel alerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicionante_id")
    private CondicionanteModel condicionante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmailDispatchStatus status = EmailDispatchStatus.PENDENTE;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String destinatarios;

    @Column(nullable = false, length = 200)
    private String assunto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String corpo;

    @Column(name = "anexos", columnDefinition = "jsonb")
    private String anexos;

    @Column(name = "template_ref", length = 120)
    private String templateRef;

    @Column(nullable = false)
    private int tentativas = 0;

    @Column(name = "max_tentativas", nullable = false)
    private int maxTentativas = 5;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "scheduled_for", nullable = false)
    private OffsetDateTime scheduledFor;

    @Column(name = "locked_at")
    private OffsetDateTime lockedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "dt_cadastro", nullable = false)
    private OffsetDateTime dtCadastro;

    @PrePersist
    public void prePersist() {
        this.dtCadastro = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public CondicionanteAlertaModel getAlerta() {
        return alerta;
    }

    public void setAlerta(CondicionanteAlertaModel alerta) {
        this.alerta = alerta;
    }

    public CondicionanteModel getCondicionante() {
        return condicionante;
    }

    public void setCondicionante(CondicionanteModel condicionante) {
        this.condicionante = condicionante;
    }

    public EmailDispatchStatus getStatus() {
        return status;
    }

    public void setStatus(EmailDispatchStatus status) {
        this.status = status;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getAnexos() {
        return anexos;
    }

    public void setAnexos(String anexos) {
        this.anexos = anexos;
    }

    public String getTemplateRef() {
        return templateRef;
    }

    public void setTemplateRef(String templateRef) {
        this.templateRef = templateRef;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    public int getMaxTentativas() {
        return maxTentativas;
    }

    public void setMaxTentativas(int maxTentativas) {
        this.maxTentativas = maxTentativas;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public OffsetDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(OffsetDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public OffsetDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(OffsetDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(OffsetDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public OffsetDateTime getDtCadastro() {
        return dtCadastro;
    }
}
