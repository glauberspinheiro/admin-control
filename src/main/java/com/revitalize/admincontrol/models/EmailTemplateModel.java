package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_EMAIL_TEMPLATE")
public class EmailTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private AdmUsuarioModel usuario;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 200)
    private String assunto;

    @Column(name = "conteudo_html", nullable = false, columnDefinition = "TEXT")
    private String conteudoHtml;

    @Column(name = "usar_assinatura", nullable = false)
    private boolean usarAssinatura = true;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDateTime dtCadastro;

    @Column(name = "dt_alteracao_cadastro")
    private LocalDateTime dtAlteracaoCadastro;

    @PrePersist
    public void onCreate() {
        this.dtCadastro = LocalDateTime.now();
        this.dtAlteracaoCadastro = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.dtAlteracaoCadastro = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public AdmUsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(AdmUsuarioModel usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudoHtml() {
        return conteudoHtml;
    }

    public void setConteudoHtml(String conteudoHtml) {
        this.conteudoHtml = conteudoHtml;
    }

    public boolean isUsarAssinatura() {
        return usarAssinatura;
    }

    public void setUsarAssinatura(boolean usarAssinatura) {
        this.usarAssinatura = usarAssinatura;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public LocalDateTime getDtAlteracaoCadastro() {
        return dtAlteracaoCadastro;
    }
}
