package com.revitalize.admincontrol.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_PRODUTO")
public class AdmProdutoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, length = 20)
    private UUID id;

    @ManyToOne
    private AdmTipoProdutoModel id_tipo_produto;

    @Column(nullable = false, length = 150)
    private String nome_produto;

    @Column(nullable = false, length = 150)
    private String medicao;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dt_cadastro;

    @Column(nullable = false)
    private LocalDateTime dt_alteracao_cadastro;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
