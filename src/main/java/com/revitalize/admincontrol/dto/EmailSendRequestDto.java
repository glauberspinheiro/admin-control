package com.revitalize.admincontrol.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public class EmailSendRequestDto {

    private UUID usuarioId;
    private UUID templateId;
    @NotBlank
    private String assunto;
    @NotBlank
    private String conteudoHtml;
    @NotEmpty
    private List<@Email String> destinatarios;
    private Boolean incluirAssinatura = Boolean.TRUE;
    private List<EmailPersonalizationDto> personalizacoes;
    private List<EmailAttachmentDto> anexos;

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = conteudoHtml;
    }

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }

    public Boolean getIncluirAssinatura() {
        return incluirAssinatura;
    }

    public void setIncluirAssinatura(Boolean incluirAssinatura) {
        this.incluirAssinatura = incluirAssinatura;
    }

    public List<EmailPersonalizationDto> getPersonalizacoes() {
        return personalizacoes;
    }

    public void setPersonalizacoes(List<EmailPersonalizationDto> personalizacoes) {
        this.personalizacoes = personalizacoes;
    }

    public List<EmailAttachmentDto> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<EmailAttachmentDto> anexos) {
        this.anexos = anexos;
    }
}
