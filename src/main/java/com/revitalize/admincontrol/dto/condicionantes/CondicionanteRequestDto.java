package com.revitalize.admincontrol.dto.condicionantes;

import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;
import com.revitalize.admincontrol.models.enums.NivelRisco;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public class CondicionanteRequestDto {

    @NotNull
    private UUID empresaId;

    @NotNull
    private UUID licencaId;

    @NotBlank
    private String titulo;

    private String descricao;

    private String categoria;

    private CondicionantePrioridade prioridade = CondicionantePrioridade.MEDIA;

    private CondicionanteStatus status = CondicionanteStatus.PLANEJADA;

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

    private List<CondicionanteSubtarefaDto> subtarefas;

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
    }

    public UUID getLicencaId() {
        return licencaId;
    }

    public void setLicencaId(UUID licencaId) {
        this.licencaId = licencaId;
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

    public List<CondicionanteSubtarefaDto> getSubtarefas() {
        return subtarefas;
    }

    public void setSubtarefas(List<CondicionanteSubtarefaDto> subtarefas) {
        this.subtarefas = subtarefas;
    }
}
