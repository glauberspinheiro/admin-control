package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.UserPreferenceModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPreferenceRepository extends CrudRepository<UserPreferenceModel, UUID> {
    Optional<UserPreferenceModel> findByUsuarioId(UUID usuarioId);
}
