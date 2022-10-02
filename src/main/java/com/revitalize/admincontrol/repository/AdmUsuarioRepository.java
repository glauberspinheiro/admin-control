package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdmUsuarioRepository extends CrudRepository<AdmUsuarioModel, UUID> {

    @Query(value = "select * from tb_usuario where email = :email and senha = :senha", nativeQuery = true)
    public AdmUsuarioModel Login(String email, String senha);

}
