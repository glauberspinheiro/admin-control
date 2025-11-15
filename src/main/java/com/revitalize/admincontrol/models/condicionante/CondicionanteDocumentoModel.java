package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.enums.DocumentoTipo;

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
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_CONDICIONANTE_DOCUMENTO")
public class CondicionanteDocumentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicionante_id", nullable = false)
    private CondicionanteModel condicionante;

    @Column(name = "nome_arquivo", nullable = false, length = 200)
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Column(length = 60)
    private DocumentoTipo tipo;

    @Column(name = "version", nullable = false)
    private Integer versao = 1;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "storage_path", length = 300)
    private String storagePath;

    @Column(length = 120)
    private String hash;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por")
    private AdmUsuarioModel validadoPor;

    @Column(name = "validado_em")
    private OffsetDateTime validadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_por")
    private AdmUsuarioModel uploadedPor;

    @Column(name = "uploaded_em", nullable = false)
    private OffsetDateTime uploadedEm;

    public UUID getId() {
        return id;
    }

    public CondicionanteModel getCondicionante() {
        return condicionante;
    }

    public void setCondicionante(CondicionanteModel condicionante) {
        this.condicionante = condicionante;
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

    public AdmUsuarioModel getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(AdmUsuarioModel validadoPor) {
        this.validadoPor = validadoPor;
    }

    public OffsetDateTime getValidadoEm() {
        return validadoEm;
    }

    public void setValidadoEm(OffsetDateTime validadoEm) {
        this.validadoEm = validadoEm;
    }

    public AdmUsuarioModel getUploadedPor() {
        return uploadedPor;
    }

    public void setUploadedPor(AdmUsuarioModel uploadedPor) {
        this.uploadedPor = uploadedPor;
    }

    public OffsetDateTime getUploadedEm() {
        return uploadedEm;
    }

    public void setUploadedEm(OffsetDateTime uploadedEm) {
        this.uploadedEm = uploadedEm;
    }
}
