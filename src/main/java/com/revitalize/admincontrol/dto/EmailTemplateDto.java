package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class EmailTemplateDto {

    private UUID usuarioId;
    @NotBlank
    private String nome;
    @NotBlank
    private String assunto;
    @NotBlank
    private String conteudoHtml;
    private Boolean usarAssinatura = Boolean.TRUE;

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public Boolean getUsarAssinatura() {
        return usarAssinatura;
    }

    public void setUsarAssinatura(Boolean usarAssinatura) {
        this.usarAssinatura = usarAssinatura;
    }
}
