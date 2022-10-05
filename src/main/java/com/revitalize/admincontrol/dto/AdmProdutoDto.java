package com.revitalize.admincontrol.dto;

import com.revitalize.admincontrol.models.AdmTipoProdutoModel;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AdmProdutoDto {
    @NotBlank
    private AdmTipoProdutoModel id_tipo_produto;
    @NotBlank
    private String nome_produto;
    @NotBlank
    private String medicao;
    @NotBlank
    private String status;
    @NotBlank
    private LocalDateTime dt_cadastro;
    @NotBlank
    private LocalDateTime dt_alteracao_cadastro;

    public AdmTipoProdutoModel getId_tipo_produto() {
        return id_tipo_produto;
    }

    public void setId_tipo_produto(AdmTipoProdutoModel id_tipo_produto) {
        this.id_tipo_produto = id_tipo_produto;
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

    public LocalDateTime getDt_cadastro() {
        return dt_cadastro;
    }

    public void setDt_cadastro(LocalDateTime dt_cadastro) {
        this.dt_cadastro = dt_cadastro;
    }

    public LocalDateTime getDt_alteracao_cadastro() {
        return dt_alteracao_cadastro;
    }

    public void setDt_alteracao_cadastro(LocalDateTime dt_alteracao_cadastro) {
        this.dt_alteracao_cadastro = dt_alteracao_cadastro;
    }
}
