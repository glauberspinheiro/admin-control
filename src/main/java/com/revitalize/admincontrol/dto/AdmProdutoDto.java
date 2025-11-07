package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class AdmProdutoDto {
    @NotNull
    private UUID tipoProdutoId;
    @NotBlank
    private String nome_produto;
    @NotBlank
    private String medicao;
    @NotBlank
    private String status;

    public UUID getTipoProdutoId() {
        return tipoProdutoId;
    }

    public void setTipoProdutoId(UUID tipoProdutoId) {
        this.tipoProdutoId = tipoProdutoId;
    }

    public String getNome_produto() {
        return nome_produto;
    }

    public void setNome_produto(String nome_produto) {
        this.nome_produto = nome_produto;
    }

    public String getMedicao() {
        return medicao;
    }

    public void setMedicao(String medicao) {
        this.medicao = medicao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
