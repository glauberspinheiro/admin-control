package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdmUsuarioRepository extends CrudRepository<AdmUsuarioModel, UUID> {
    Optional<AdmUsuarioModel> findByEmailAndSenha(String email, String senha);

    Optional<AdmUsuarioModel> findById(UUID id);

    boolean findAllById(UUID id);

    Optional<AdmUsuarioModel> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
