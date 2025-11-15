package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.enums.CondicionanteAlertaStatus;

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
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "TB_CONDICIONANTE_ALERTA_LOG")
public class CondicionanteAlertaLogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alerta_id", nullable = false)
    private CondicionanteAlertaModel alerta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CondicionanteAlertaStatus status;

    @Column(columnDefinition = "TEXT")
    private String detalhe;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    public Long getId() {
        return id;
    }

    public CondicionanteAlertaModel getAlerta() {
        return alerta;
    }

    public void setAlerta(CondicionanteAlertaModel alerta) {
        this.alerta = alerta;
    }

    public CondicionanteAlertaStatus getStatus() {
        return status;
    }

    public void setStatus(CondicionanteAlertaStatus status) {
        this.status = status;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
