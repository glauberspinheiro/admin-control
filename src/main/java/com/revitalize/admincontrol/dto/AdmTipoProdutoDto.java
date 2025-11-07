package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotBlank;

public class AdmTipoProdutoDto {


    @NotBlank
    private String descricao;
    @NotBlank
    private String tipo;
    @NotBlank
    private String status;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
