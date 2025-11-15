package com.revitalize.admincontrol.dto.condicionantes;

import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.models.enums.NivelRisco;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public class CondicionanteDto {

    private UUID id;
    private UUID empresaId;
    private String empresaNome;
    private UUID licencaId;
    private String licencaNumero;
    private String titulo;
    private String descricao;
    private String categoria;
    private CondicionantePrioridade prioridade;
    private CondicionanteStatus status;
    private BigDecimal riscoScore;
    private NivelRisco riscoClassificacao;
    private UUID responsavelId;
    private String responsavelEmail;
    private String gestorEmail;
    private List<String> destinatarios;
    private LocalDate dataInicio;
    private LocalDate vencimento;
    private Integer slaDias;
    private List<Integer> janelasPadrao;
    private List<String> tags;
    private OffsetDateTime dtCadastro;
    private OffsetDateTime dtAlteracao;
    private List<CondicionanteSubtarefaDto> subtarefas;
    private List<CondicionanteDocumentoDto> documentos;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }

    public UUID getLicencaId() {
        return licencaId;
    }

    public void setLicencaId(UUID licencaId) {
        this.licencaId = licencaId;
    }

    public String getLicencaNumero() {
        return licencaNumero;
    }

    public void setLicencaNumero(String licencaNumero) {
        this.licencaNumero = licencaNumero;
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

    public UUID getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(UUID responsavelId) {
        this.responsavelId = responsavelId;
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

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
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

    public List<Integer> getJanelasPadrao() {
        return janelasPadrao;
    }

    public void setJanelasPadrao(List<Integer> janelasPadrao) {
        this.janelasPadrao = janelasPadrao;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public OffsetDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(OffsetDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public OffsetDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(OffsetDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }

    public List<CondicionanteSubtarefaDto> getSubtarefas() {
        return subtarefas;
    }

    public void setSubtarefas(List<CondicionanteSubtarefaDto> subtarefas) {
        this.subtarefas = subtarefas;
    }

    public List<CondicionanteDocumentoDto> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<CondicionanteDocumentoDto> documentos) {
        this.documentos = documentos;
    }
}
