package com.revitalize.admincontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_USUARIO")
//Tabela de usuário
public class AdmUsuarioModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, length = 20)
    private UUID id;

    @Column(nullable = true, length = 11)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String senha;

    @JsonIgnore
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreferenceModel preference;

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    public UserPreferenceModel getPreference() {
        return preference;
    }

    public void setPreference(UserPreferenceModel preference) {
        this.preference = preference;
    }
}
