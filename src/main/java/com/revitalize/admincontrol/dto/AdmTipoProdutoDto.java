package com.revitalize.admincontrol.dto;

import com.revitalize.admincontrol.models.AdmProdutoModel;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class AdmTipoProdutoDto {

    @NotBlank
    private AdmProdutoModel id_produto;
    @NotBlank
    private String nome;
    @NotBlank
    private String classe;
    @NotBlank
    private String status;
    @NotBlank
    private LocalDateTime dt_cadastro;
    @NotBlank
    private LocalDateTime dt_alteracao_cadastro;

    public AdmProdutoModel getId_produto() {
        return id_produto;
    }

    public void setId_produto(AdmProdutoModel id_produto) {
        this.id_produto = id_produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
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
