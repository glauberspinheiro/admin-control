package com.revitalize.admincontrol.dto.condicionantes;

import com.revitalize.admincontrol.models.enums.SubtarefaStatus;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

public class CondicionanteSubtarefaDto {

    private UUID id;

    @NotBlank
    private String titulo;

    private String descricao;
    private UUID responsavelId;
    private String responsavelNome;
    private String responsavelEmail;
    private SubtarefaStatus status = SubtarefaStatus.PENDENTE;
    private Integer ordem = 0;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(UUID responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public SubtarefaStatus getStatus() {
        return status;
    }

    public void setStatus(SubtarefaStatus status) {
        this.status = status;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
