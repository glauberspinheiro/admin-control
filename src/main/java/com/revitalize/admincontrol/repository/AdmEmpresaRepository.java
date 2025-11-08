package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdmEmpresaRepository extends JpaRepository<AdmEmpresaModel, UUID> {

    Optional<AdmEmpresaModel> findByCnpj(String cnpj);
}
