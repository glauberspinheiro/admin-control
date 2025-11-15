package com.revitalize.admincontrol.dto.condicionantes;

import com.revitalize.admincontrol.models.enums.CondicionantePrioridade;
import com.revitalize.admincontrol.models.enums.CondicionanteStatus;

import java.time.LocalDate;
import java.util.UUID;

public class CondicionanteResumoDto {

    private UUID id;
    private String titulo;
    private String empresa;
    private CondicionantePrioridade prioridade;
    private CondicionanteStatus status;
    private LocalDate vencimento;

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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
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

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }
}
