package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "TB_KANBAN_BOARD")
public class KanbanBoardModel implements Serializable {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String configuracao;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "dt_alteracao_cadastro", nullable = false)
    private LocalDateTime dtAlteracaoCadastro;

    @PrePersist
    public void prePersist() {
        if (dtCadastro == null) {
            dtCadastro = LocalDateTime.now(SAO_PAULO);
        }
        if (dtAlteracaoCadastro == null) {
            dtAlteracaoCadastro = dtCadastro;
        }
    }

    @PreUpdate
    public void preUpdate() {
        dtAlteracaoCadastro = LocalDateTime.now(SAO_PAULO);
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(String configuracao) {
        this.configuracao = configuracao;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public LocalDateTime getDtAlteracaoCadastro() {
        return dtAlteracaoCadastro;
    }

    public void setDtAlteracaoCadastro(LocalDateTime dtAlteracaoCadastro) {
        this.dtAlteracaoCadastro = dtAlteracaoCadastro;
    }
}
