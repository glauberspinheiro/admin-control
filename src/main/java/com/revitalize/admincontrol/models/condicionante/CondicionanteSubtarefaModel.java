package com.revitalize.admincontrol.models.condicionante;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.enums.SubtarefaStatus;

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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_CONDICIONANTE_SUBTAREFA")
public class CondicionanteSubtarefaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicionante_id", nullable = false)
    private CondicionanteModel condicionante;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private AdmUsuarioModel responsavel;

    @Column(name = "responsavel_nome", length = 120)
    private String responsavelNome;

    @Column(name = "responsavel_email", length = 150)
    private String responsavelEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubtarefaStatus status = SubtarefaStatus.PENDENTE;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "dt_cadastro", nullable = false)
    private OffsetDateTime dtCadastro;

    @Column(name = "dt_alteracao_cadastro", nullable = false)
    private OffsetDateTime dtAlteracaoCadastro;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        dtCadastro = now;
        dtAlteracaoCadastro = now;
    }

    @PreUpdate
    public void preUpdate() {
        dtAlteracaoCadastro = OffsetDateTime.now();
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AdmUsuarioModel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(AdmUsuarioModel responsavel) {
        this.responsavel = responsavel;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getResponsavelEmail() {
        return responsavelEmail;
    }

    public void setResponsavelEmail(String responsavelEmail) {
        this.responsavelEmail = responsavelEmail;
    }

    public SubtarefaStatus getStatus() {
        return status;
    }

    public void setStatus(SubtarefaStatus status) {
        this.status = status;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public OffsetDateTime getDtCadastro() {
        return dtCadastro;
    }

    public OffsetDateTime getDtAlteracaoCadastro() {
        return dtAlteracaoCadastro;
    }
}
