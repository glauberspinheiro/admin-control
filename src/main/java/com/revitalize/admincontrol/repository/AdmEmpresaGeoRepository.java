package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdmEmpresaGeoRepository extends JpaRepository<AdmEmpresaModel, UUID> {
    Page<AdmEmpresaModel> findByLatIsNullOrLngIsNull(Pageable pageable);
}
