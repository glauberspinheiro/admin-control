package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotBlank;

public class EmailAttachmentDto {

    @NotBlank
    private String nomeArquivo;

    private String contentType;

    @NotBlank
    private String conteudoBase64;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getConteudoBase64() {
        return conteudoBase64;
    }

    public void setConteudoBase64(String conteudoBase64) {
        this.conteudoBase64 = conteudoBase64;
    }
}
