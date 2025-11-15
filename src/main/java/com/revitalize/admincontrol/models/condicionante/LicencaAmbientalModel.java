package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.enums.NivelRisco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_LICENCA_AMBIENTAL")
public class LicencaAmbientalModel {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private AdmEmpresaModel empresa;

    @Column(nullable = false, length = 80)
    private String numero;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(name = "orgao_emissor", length = 150)
    private String orgaoEmissor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(length = 30)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_risco", length = 30)
    private NivelRisco nivelRisco;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "dt_cadastro", nullable = false)
    private OffsetDateTime dtCadastro;

    @Column(name = "dt_alteracao_cadastro", nullable = false)
    private OffsetDateTime dtAlteracaoCadastro;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.dtCadastro = now;
        this.dtAlteracaoCadastro = now;
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = "ATIVA";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dtAlteracaoCadastro = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AdmEmpresaModel getEmpresa() {
        return empresa;
    }

    public void setEmpresa(AdmEmpresaModel empresa) {
        this.empresa = empresa;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getOrgaoEmissor() {
        return orgaoEmissor;
    }

    public void setOrgaoEmissor(String orgaoEmissor) {
        this.orgaoEmissor = orgaoEmissor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public OffsetDateTime getDtCadastro() {
        return dtCadastro;
    }

    public OffsetDateTime getDtAlteracaoCadastro() {
        return dtAlteracaoCadastro;
    }
}
