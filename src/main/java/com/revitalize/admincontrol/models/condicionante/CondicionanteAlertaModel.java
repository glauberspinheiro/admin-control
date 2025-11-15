package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.enums.CondicionanteAlertaStatus;
import com.revitalize.admincontrol.models.enums.CondicionanteAlertaTipo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_CONDICIONANTE_ALERTA")
public class CondicionanteAlertaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicionante_id", nullable = false)
    private CondicionanteModel condicionante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CondicionanteAlertaTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionanteAlertaStatus status = CondicionanteAlertaStatus.PENDENTE;

    @Column(length = 30)
    private String canal = "EMAIL";

    @Column(name = "janela_dias")
    private Integer janelaDias;

    @Column(name = "escalonado")
    private boolean escalonado;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String destinatarios;

    @Column(length = 200)
    private String assunto;

    @Column(columnDefinition = "TEXT")
    private String corpo;

    @Column(name = "disparo_previsto", nullable = false)
    private OffsetDateTime disparoPrevisto;

    @Column(name = "disparo_executado")
    private OffsetDateTime disparoExecutado;

    @Column(nullable = false)
    private int tentativas = 0;

    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;

    @Column(name = "dt_cadastro", nullable = false)
    private OffsetDateTime dtCadastro;

    @PrePersist
    public void prePersist() {
        this.dtCadastro = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public CondicionanteModel getCondicionante() {
        return condicionante;
    }

    public void setCondicionante(CondicionanteModel condicionante) {
        this.condicionante = condicionante;
    }

    public CondicionanteAlertaTipo getTipo() {
        return tipo;
    }

    public void setTipo(CondicionanteAlertaTipo tipo) {
        this.tipo = tipo;
    }

    public CondicionanteAlertaStatus getStatus() {
        return status;
    }

    public void setStatus(CondicionanteAlertaStatus status) {
        this.status = status;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Integer getJanelaDias() {
        return janelaDias;
    }

    public void setJanelaDias(Integer janelaDias) {
        this.janelaDias = janelaDias;
    }

    public boolean isEscalonado() {
        return escalonado;
    }

    public void setEscalonado(boolean escalonado) {
        this.escalonado = escalonado;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(String destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public OffsetDateTime getDisparoPrevisto() {
        return disparoPrevisto;
    }

    public void setDisparoPrevisto(OffsetDateTime disparoPrevisto) {
        this.disparoPrevisto = disparoPrevisto;
    }

    public OffsetDateTime getDisparoExecutado() {
        return disparoExecutado;
    }

    public void setDisparoExecutado(OffsetDateTime disparoExecutado) {
        this.disparoExecutado = disparoExecutado;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public OffsetDateTime getDtCadastro() {
        return dtCadastro;
    }
}
