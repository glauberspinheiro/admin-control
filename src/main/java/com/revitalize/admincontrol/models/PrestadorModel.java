package com.revitalize.admincontrol.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PRESTADOR")
public class PrestadorModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255)
    private String nome;

    @Column(nullable=false, length=80)
    private String tipo; // 'logistica_reversa' | 'transporte_residuo' | ...

    @Column(nullable=false)
    private Double lat;

    @Column(nullable=false)
    private Double lng;

    @Column(name="dt_cadastro", nullable=false)
    private LocalDateTime dtCadastro = LocalDateTime.now();

    @Column(name="dt_alteracao_cadastro", nullable=false)
    private LocalDateTime dtAlteracaoCadastro = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() { dtAlteracaoCadastro = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setLat(Double lat) { this.lat = lat; }
    public void setLng(Double lng) { this.lng = lng; }
}
