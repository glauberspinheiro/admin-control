package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.condicionante.CondicionanteDocumentoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CondicionanteDocumentoRepository extends JpaRepository<CondicionanteDocumentoModel, UUID> {
    List<CondicionanteDocumentoModel> findByCondicionanteIdOrderByVersaoDesc(UUID condicionanteId);

    Optional<CondicionanteDocumentoModel> findFirstByCondicionanteIdOrderByVersaoDesc(UUID condicionanteId);
}
