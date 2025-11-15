package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.LicencaAmbientalModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LicencaAmbientalRepository extends JpaRepository<LicencaAmbientalModel, UUID> {
    List<LicencaAmbientalModel> findByEmpresaId(UUID empresaId);
}
