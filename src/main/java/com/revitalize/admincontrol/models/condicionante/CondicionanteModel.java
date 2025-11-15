package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.models.enums.NivelRisco;

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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "TB_CONDICIONANTE")
public class CondicionanteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private AdmEmpresaModel empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licenca_id", nullable = false)
    private LicencaAmbientalModel licenca;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 60)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionantePrioridade prioridade = CondicionantePrioridade.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionanteStatus status = CondicionanteStatus.PLANEJADA;

    @Column(name = "risco_score", precision = 5, scale = 2)
    private BigDecimal riscoScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risco_classificacao", length = 30)
    private NivelRisco riscoClassificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private AdmUsuarioModel responsavel;

    @Column(name = "responsavel_email", length = 150)
    private String responsavelEmail;

    @Column(name = "gestor_email", length = 150)
    private String gestorEmail;

    @Column(columnDefinition = "TEXT")
    private String destinatarios;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "vencimento")
    private LocalDate vencimento;

    @Column(name = "sla_dias")
    private Integer slaDias;

    @Column(name = "janela_alerta_padrao")
    private String janelaAlertaPadrao;

    @Column(length = 200)
    private String tags;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "dt_cadastro", nullable = false)
    private OffsetDateTime dtCadastro;

    @Column(name = "dt_alteracao_cadastro", nullable = false)
    private OffsetDateTime dtAlteracaoCadastro;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        dtCadastro = now;
        dtAlteracaoCadastro = now;
    }

    @PreUpdate
    public void preUpdate() {
        dtAlteracaoCadastro = OffsetDateTime.now();
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

    public LicencaAmbientalModel getLicenca() {
        return licenca;
    }

    public void setLicenca(LicencaAmbientalModel licenca) {
        this.licenca = licenca;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public CondicionantePrioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(CondicionantePrioridade prioridade) {
        this.prioridade = prioridade;
    }

    public CondicionanteStatus getStatus() {
        return status;
    }

    public void setStatus(CondicionanteStatus status) {
        this.status = status;
    }

    public BigDecimal getRiscoScore() {
        return riscoScore;
    }

    public void setRiscoScore(BigDecimal riscoScore) {
        this.riscoScore = riscoScore;
    }

    public NivelRisco getRiscoClassificacao() {
        return riscoClassificacao;
    }

    public void setRiscoClassificacao(NivelRisco riscoClassificacao) {
        this.riscoClassificacao = riscoClassificacao;
    }

    public AdmUsuarioModel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(AdmUsuarioModel responsavel) {
        this.responsavel = responsavel;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public String getGestorEmail() {
        return gestorEmail;
    }

    public void setGestorEmail(String gestorEmail) {
        this.gestorEmail = gestorEmail;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public Integer getSlaDias() {
        return slaDias;
    }

    public void setSlaDias(Integer slaDias) {
        this.slaDias = slaDias;
    }

    public String getJanelaAlertaPadrao() {
        return janelaAlertaPadrao;
    }

    public void setJanelaAlertaPadrao(String janelaAlertaPadrao) {
        this.janelaAlertaPadrao = janelaAlertaPadrao;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
