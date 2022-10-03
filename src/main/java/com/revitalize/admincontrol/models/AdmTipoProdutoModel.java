package com.revitalize.admincontrol.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "TB_USUARIO")
public class AdmTipoProduto implements Serializable {

        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(nullable = false, unique = true, length = 20)
        private UUID id;

        @Column(nullable = false, length = 11)
        private String nome;

        @Column(nullable = false, length = 100)
        private String nome;
}
