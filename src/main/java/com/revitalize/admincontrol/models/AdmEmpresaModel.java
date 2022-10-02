package com.revitalize.admincontrol.models;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "TB_EMPRESA")
public class AdmEmpresaModel {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, length = 20)
    private UUID id;

    @Column(nullable = false, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 150)
    private String nomeempresa;

    @Column(nullable = false, length = 150)
    private String nomefantasia;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 1)
    private String mensalista;

    @Column(nullable = false, length = 11)
    private String telefone;

    @Column(nullable = false, length = 100)
    private String contato;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dt_cadastro;

    @Column(nullable = false)
    private LocalDateTime dt_alteracao_cadastro;

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeempresa() {
        return nomeempresa;
    }

    public void setNomeempresa(String nomeempresa) {
        this.nomeempresa = nomeempresa;
    }

    public String getNomefantasia() {
        return nomefantasia;
    }

    public void setNomefantasia(String nomefantasia) {
        this.nomefantasia = nomefantasia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMensalista() {
        return mensalista;
    }

    public void setMensalista(String mensalista) {
        this.mensalista = mensalista;
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
