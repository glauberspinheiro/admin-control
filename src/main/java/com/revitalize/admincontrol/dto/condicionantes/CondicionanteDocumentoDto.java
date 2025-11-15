package com.revitalize.admincontrol.dto.condicionantes;

import com.revitalize.admincontrol.models.enums.DocumentoTipo;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CondicionanteDocumentoDto {

    private UUID id;
    private String nomeArquivo;
    private DocumentoTipo tipo;
    private Integer versao;
    private Long tamanhoBytes;
    private String contentType;
    private String storagePath;
    private String hash;
    private String observacoes;
    private OffsetDateTime uploadedEm;
    private OffsetDateTime validadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public DocumentoTipo getTipo() {
        return tipo;
    }

    public void setTipo(DocumentoTipo tipo) {
        this.tipo = tipo;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public OffsetDateTime getUploadedEm() {
        return uploadedEm;
    }

    public void setUploadedEm(OffsetDateTime uploadedEm) {
        this.uploadedEm = uploadedEm;
    }

    public OffsetDateTime getValidadoEm() {
        return validadoEm;
    }

    public void setValidadoEm(OffsetDateTime validadoEm) {
        this.validadoEm = validadoEm;
    }
}
