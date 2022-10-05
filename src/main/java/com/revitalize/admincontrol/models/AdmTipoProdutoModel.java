package com.revitalize.admincontrol.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_TIPO_PRODUTO")
public class AdmTipoProdutoModel implements Serializable {

        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(nullable = false, unique = true, length = 20)
        private UUID id;

        @Column(nullable = false, length = 100)
        private String nome;

        @Column(nullable = false, length = 80)
        private String classe;

        @Column(nullable = false, length = 1)
        private String status;

        @Column(nullable = false)
        private LocalDateTime dt_cadastro;

        @Column(nullable = false)
        private LocalDateTime dt_alteracao_cadastro;


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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
