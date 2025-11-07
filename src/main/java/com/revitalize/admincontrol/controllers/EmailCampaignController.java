package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailSendRequestDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailJobModel;
import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.models.enums.EmailJobStatus;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.EmailCampaignService;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import com.revitalize.admincontrol.services.EmailTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/email/campaigns")
public class EmailCampaignController {

    private final EmailCampaignService emailCampaignService;
    private final EmailTemplateService emailTemplateService;
    private final EmailServerConfigService emailServerConfigService;
    private final AdmUsuarioRepository admUsuarioRepository;

    public EmailCampaignController(EmailCampaignService emailCampaignService,
                                   EmailTemplateService emailTemplateService,
                                   EmailServerConfigService emailServerConfigService,
                                   AdmUsuarioRepository admUsuarioRepository) {
        this.emailCampaignService = emailCampaignService;
        this.emailTemplateService = emailTemplateService;
        this.emailServerConfigService = emailServerConfigService;
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EmailJobModel>> history(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailCampaignService.findByUsuario(userId));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody @Valid EmailSendRequestDto dto) {
        AdmUsuarioModel usuario = admUsuarioRepository.findById(dto.getUsuarioId())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        EmailServerConfigModel config = emailServerConfigService.findByUsuarioId(usuario.getId())
                .orElse(null);
        if (config == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Configuração de servidor de e-mail não encontrada para o usuário");
        }

        EmailTemplateModel template = null;
        if (dto.getTemplateId() != null) {
            template = emailTemplateService.findById(dto.getTemplateId()).orElse(null);
            if (template == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template informado não encontrado");
            }
        }

        String preview = dto.getConteudoHtml() == null ? "" : dto.getConteudoHtml().replaceAll("<[^>]*>", "").trim();
        if (preview.length() > 120) {
            preview = preview.substring(0, 120);
        }

        EmailJobModel job = emailCampaignService.registerJob(
                usuario,
                template,
                dto.getAssunto(),
                preview,
                dto.getDestinatarios());

        boolean incluirAssinatura = dto.getIncluirAssinatura() == null || dto.getIncluirAssinatura();

        try {
            emailCampaignService.sendEmails(
                    config,
                    dto.getAssunto(),
                    dto.getConteudoHtml(),
                    dto.getDestinatarios(),
                    dto.getPersonalizacoes(),
                    dto.getAnexos(),
                    incluirAssinatura);
            emailCampaignService.updateJobStatus(job, EmailJobStatus.ENVIADO);
            return ResponseEntity.ok(job);
        } catch (RuntimeException ex) {
            emailCampaignService.updateJobStatus(job, EmailJobStatus.FALHOU);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao enviar e-mails: " + ex.getMessage());
        }
    }
}
