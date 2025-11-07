package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.repository.EmailTemplateRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    public EmailTemplateService(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
    }

    public List<EmailTemplateModel> findByUsuario(UUID usuarioId) {
        return emailTemplateRepository.findByUsuarioId(usuarioId);
    }

    public Optional<EmailTemplateModel> findById(UUID id) {
        return emailTemplateRepository.findById(id);
    }

    @Transactional
    public EmailTemplateModel save(EmailTemplateModel template) {
        return emailTemplateRepository.save(template);
    }

    @Transactional
    public void delete(UUID id) {
        emailTemplateRepository.deleteById(id);
    }
}
