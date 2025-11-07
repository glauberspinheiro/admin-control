package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_EMAIL_SERVER_CONFIG")
public class EmailServerConfigModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private AdmUsuarioModel usuario;

    @Column(name = "smtp_host", nullable = false, length = 150)
    private String smtpHost;

    @Column(name = "smtp_port", nullable = false)
    private Integer smtpPort;

    @Column(name = "smtp_username", nullable = false, length = 150)
    private String smtpUsername;

    @Column(name = "smtp_password", nullable = false, length = 200)
    private String smtpPassword;

    @Column(name = "smtp_protocol", nullable = false, length = 20)
    private String smtpProtocol = "smtp";

    @Column(name = "pop_host", length = 150)
    private String popHost;

    @Column(name = "pop_port")
    private Integer popPort;

    @Column(name = "imap_host", length = 150)
    private String imapHost;

    @Column(name = "imap_port")
    private Integer imapPort;

    @Column(name = "use_ssl", nullable = false)
    private boolean useSsl = false;

    @Column(name = "use_starttls", nullable = false)
    private boolean useStartTls = true;

    @Column(name = "signature_html")
    private String signatureHtml;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getSmtpProtocol() {
        return smtpProtocol;
    }

    public void setSmtpProtocol(String smtpProtocol) {
        this.smtpProtocol = smtpProtocol;
    }

    public String getPopHost() {
        return popHost;
    }

    public void setPopHost(String popHost) {
        this.popHost = popHost;
    }

    public Integer getPopPort() {
        return popPort;
    }

    public void setPopPort(Integer popPort) {
        this.popPort = popPort;
    }

    public String getImapHost() {
        return imapHost;
    }

    public void setImapHost(String imapHost) {
        this.imapHost = imapHost;
    }

    public Integer getImapPort() {
        return imapPort;
    }

    public void setImapPort(Integer imapPort) {
        this.imapPort = imapPort;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public boolean isUseStartTls() {
        return useStartTls;
    }

    public void setUseStartTls(boolean useStartTls) {
        this.useStartTls = useStartTls;
    }

    public String getSignatureHtml() {
        return signatureHtml;
    }

    public void setSignatureHtml(String signatureHtml) {
        this.signatureHtml = signatureHtml;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
