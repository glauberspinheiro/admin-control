package com.revitalize.admincontrol.dto;

import org.hibernate.validator.constraints.br.CNPJ;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class AdmEmpresaDto {

    @Size(max=11)
    @CNPJ
    @NotBlank
    private String cnpj;
    @NotBlank
    private String nomeempresa;
    @NotBlank
    private String nomefantasia;
    @NotBlank
    private String email;
    @NotBlank
    private String contato;
    @NotBlank
    private String telefone;
    @NotBlank
    private String mensalista;
    @NotBlank
    private String status;
    @NotBlank
    private LocalDateTime dt_cadastro;
    @NotBlank
    private LocalDateTime dt_alteracao_cadastro;

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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
