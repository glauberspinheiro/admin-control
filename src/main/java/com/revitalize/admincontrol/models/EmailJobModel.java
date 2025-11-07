package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revitalize.admincontrol.models.enums.EmailJobStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_EMAIL_JOB")
public class EmailJobModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private AdmUsuarioModel usuario;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplateModel template;

    @Column(nullable = false, length = 200)
    private String assunto;

    @Column(name = "mensagem_preview")
    private String mensagemPreview;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String destinatarios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EmailJobStatus status = EmailJobStatus.PENDENTE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public AdmUsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(AdmUsuarioModel usuario) {
        this.usuario = usuario;
    }

    public EmailTemplateModel getTemplate() {
        return template;
    }

    public void setTemplate(EmailTemplateModel template) {
        this.template = template;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagemPreview() {
        return mensagemPreview;
    }

    public void setMensagemPreview(String mensagemPreview) {
        this.mensagemPreview = mensagemPreview;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public EmailJobStatus getStatus() {
        return status;
    }

    public void setStatus(EmailJobStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
