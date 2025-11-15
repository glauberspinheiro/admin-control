package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.repository.EmailServerConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmailServerConfigService {

    private final EmailServerConfigRepository emailServerConfigRepository;

    public EmailServerConfigService(EmailServerConfigRepository emailServerConfigRepository) {
        this.emailServerConfigRepository = emailServerConfigRepository;
    }

    public Optional<EmailServerConfigModel> findByUsuarioId(UUID usuarioId) {
        return emailServerConfigRepository.findByUsuarioId(usuarioId);
    }

    public boolean hasConfig(UUID usuarioId) {
        return emailServerConfigRepository.existsByUsuarioId(usuarioId);
    }

    @Transactional
    public EmailServerConfigModel save(EmailServerConfigModel config) {
        return emailServerConfigRepository.save(config);
    }
}
