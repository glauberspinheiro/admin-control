package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailSendRequestDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailJobModel;
import com.revitalize.admincontrol.security.UserAccessService;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.EmailCampaignService;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import com.revitalize.admincontrol.services.EmailTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/email/campaigns")
public class EmailCampaignController {

    private final EmailCampaignService emailCampaignService;
    private final EmailTemplateService emailTemplateService;
    private final EmailServerConfigService emailServerConfigService;
    private final AdmUsuarioService admUsuarioService;
    private final UserAccessService userAccessService;

    public EmailCampaignController(EmailCampaignService emailCampaignService,
                                   EmailTemplateService emailTemplateService,
                                   EmailServerConfigService emailServerConfigService,
                                   AdmUsuarioService admUsuarioService,
                                   UserAccessService userAccessService) {
        this.emailCampaignService = emailCampaignService;
        this.emailTemplateService = emailTemplateService;
        this.emailServerConfigService = emailServerConfigService;
        this.admUsuarioService = admUsuarioService;
        this.userAccessService = userAccessService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<EmailJobModel>> history(@PathVariable("userId") UUID userId,
                                                       @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, userId);
        return ResponseEntity.ok(emailCampaignService.findByUsuario(targetUserId));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody @Valid EmailSendRequestDto dto,
                                  @AuthenticationPrincipal UserDetails principal) {
        try {
            AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
            UUID targetUserId = userAccessService.resolveTargetUserId(requester, dto.getUsuarioId());
            AdmUsuarioModel usuario = admUsuarioService.findById(targetUserId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Delega toda a lógica para o serviço
            EmailJobModel job = emailCampaignService.scheduleCampaign(dto, usuario);
            
            // Retorna 202 Accepted para indicar que a requisição foi aceita
            // e está sendo processada em background.
            return ResponseEntity.accepted().body(job);
            
        } catch (RuntimeException ex) { // Idealmente capturar exceções mais específicas
            // Trata erros de validação que acontecem antes do processamento assíncrono
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
