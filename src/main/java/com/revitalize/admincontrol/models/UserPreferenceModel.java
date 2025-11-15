package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_USER_PREFERENCE")
public class UserPreferenceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private AdmUsuarioModel usuario;

    @Column(nullable = false, length = 32)
    private String theme = "bluelight";

    @Column(nullable = false, length = 10)
    private String language = "pt-BR";

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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public LocalDateTime getDtAlteracaoCadastro() {
        return dtAlteracaoCadastro;
    }
}
