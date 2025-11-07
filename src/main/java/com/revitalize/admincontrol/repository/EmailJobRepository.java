package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.EmailJobModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EmailJobRepository extends CrudRepository<EmailJobModel, UUID> {
    List<EmailJobModel> findByUsuarioId(UUID usuarioId);
}
