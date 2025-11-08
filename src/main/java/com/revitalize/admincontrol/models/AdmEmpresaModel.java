package com.revitalize.admincontrol.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "TB_EMPRESA")
public class AdmEmpresaModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true, length = 20)
    private UUID id;

    @Column(nullable = false, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 150)
    private String nomeempresa;

    @Column(nullable = true, length = 150)
    private String nomefantasia;

    @Column(nullable = true, length = 150)
    private String email;

    @Column(nullable = true, length = 1)
    private String mensalista;

    @Column(nullable = true, length = 20)
    private String telefone;

    @Column(nullable = true, length = 100)
    private String contato;

    @Column(nullable = false, length = 1)
    private String status;

    @Column(nullable = false)
    private LocalDateTime dt_cadastro;

    @Column(nullable = false)
    private LocalDateTime dt_alteracao_cadastro;

    @Column(nullable = true, length = 9)
    private String cep;

    @Column(nullable = true, length = 150)
    private String logradouro;

    @Column(nullable = true, length = 20)
    private String numero;

    @Column(nullable = true, length = 150)
    private String complemento;

    @Column(nullable = true, length = 150)
    private String bairro;

    @Column(nullable = true, length = 150)
    private String municipio;

    @Column(nullable = true, length = 2)
    private String uf;

    @Column(nullable = true, length = 160)
    private String regimeTributario;

    @Column(nullable = true, length = 255)
    private String atividadePrincipal;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String atividadesSecundarias;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String socios;

    @PrePersist
    public void prePersist() {
        if (dt_cadastro == null) {
            dt_cadastro = LocalDateTime.now(SAO_PAULO);
        }
        if (dt_alteracao_cadastro == null) {
            dt_alteracao_cadastro = dt_cadastro;
        }
        applyDefaults();
    }

    @PreUpdate
    public void preUpdate() {
        dt_alteracao_cadastro = LocalDateTime.now(SAO_PAULO);
        applyDefaults();
    }

    private void applyDefaults() {
        if (mensalista == null || mensalista.isBlank()) {
            mensalista = "N";
        }
        if (status == null || status.isBlank()) {
            status = "A";
        }
    }

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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(String regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public String getAtividadePrincipal() {
        return atividadePrincipal;
    }

    public void setAtividadePrincipal(String atividadePrincipal) {
        this.atividadePrincipal = atividadePrincipal;
    }

    public String getAtividadesSecundarias() {
        return atividadesSecundarias;
    }

    public void setAtividadesSecundarias(String atividadesSecundarias) {
        this.atividadesSecundarias = atividadesSecundarias;
    }

    public String getSocios() {
        return socios;
    }

    public void setSocios(String socios) {
        this.socios = socios;
    }
}
