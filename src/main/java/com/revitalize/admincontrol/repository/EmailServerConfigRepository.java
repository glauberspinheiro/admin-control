package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.EmailServerConfigModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailServerConfigRepository extends CrudRepository<EmailServerConfigModel, UUID> {
    Optional<EmailServerConfigModel> findByUsuarioId(UUID usuarioId);
    boolean existsByUsuarioId(UUID usuarioId);
}
