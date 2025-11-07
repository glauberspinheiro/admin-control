package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.EmailTemplateModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplateModel, UUID> {
    List<EmailTemplateModel> findByUsuarioId(UUID usuarioId);
}
