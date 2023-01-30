package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmEmpresaRepository extends CrudRepository<AdmEmpresaModel, UUID>{

   @Query(value = "select * from tb_empreda where id = :id", nativeQuery = true)
   public AdmEmpresaModel cadastroEmpresa(UUID id);

   }
