package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.UserPreferenceModel;
import com.revitalize.admincontrol.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public Optional<UserPreferenceModel> findByUsuarioId(UUID usuarioId) {
        return userPreferenceRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public UserPreferenceModel getOrCreate(AdmUsuarioModel usuario) {
        return userPreferenceRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    UserPreferenceModel preference = new UserPreferenceModel();
                    preference.setUsuario(usuario);
                    preference.setTheme("bluelight");
                    preference.setLanguage("pt-BR");
                    return userPreferenceRepository.save(preference);
                });
    }

    @Transactional
    public UserPreferenceModel save(UserPreferenceModel preference) {
        return userPreferenceRepository.save(preference);
    }
}
