package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailServerConfigDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.security.UserAccessService;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import org.springframework.beans.BeanUtils;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/email/server-config")
public class EmailServerConfigController {

    private final EmailServerConfigService emailServerConfigService;
    private final AdmUsuarioService admUsuarioService;
    private final UserAccessService userAccessService;

    public EmailServerConfigController(EmailServerConfigService emailServerConfigService,
                                       AdmUsuarioService admUsuarioService,
                                       UserAccessService userAccessService) {
        this.emailServerConfigService = emailServerConfigService;
        this.admUsuarioService = admUsuarioService;
        this.userAccessService = userAccessService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable("userId") UUID userId,
                                       @AuthenticationPrincipal UserDetails principal) {
        var requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, userId);
        return emailServerConfigService.findByUsuarioId(targetUserId)
                .map(config -> ResponseEntity.ok(mapToDto(config)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid EmailServerConfigDto dto,
                                  @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, dto.getUsuarioId());
        AdmUsuarioModel usuario = admUsuarioService.findById(targetUserId)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        EmailServerConfigModel config = emailServerConfigService.findByUsuarioId(targetUserId)
                .orElseGet(() -> {
                    EmailServerConfigModel model = new EmailServerConfigModel();
                    model.setUsuario(usuario);
                    return model;
                });
        BeanUtils.copyProperties(dto, config, "usuarioId", "smtpPassword", "passwordSet");
        config.setUsuario(usuario);
        boolean updatingPassword = dto.getSmtpPassword() != null && !dto.getSmtpPassword().isBlank();
        if (updatingPassword) {
            config.setSmtpPassword(dto.getSmtpPassword());
        } else if (config.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Senha do servidor SMTP é obrigatória");
        }
        if (config.getSmtpProtocol() == null || config.getSmtpProtocol().isBlank()) {
            config.setSmtpProtocol("smtp");
        }
        return ResponseEntity.ok(mapToDto(emailServerConfigService.save(config)));
    }

    private EmailServerConfigDto mapToDto(EmailServerConfigModel config) {
        EmailServerConfigDto dto = new EmailServerConfigDto();
        dto.setUsuarioId(config.getUsuario().getId());
        dto.setSmtpHost(config.getSmtpHost());
        dto.setSmtpPort(config.getSmtpPort());
        dto.setSmtpUsername(config.getSmtpUsername());
        dto.setSmtpPassword(config.getSmtpPassword());
        dto.setSmtpProtocol(config.getSmtpProtocol());
        dto.setPopHost(config.getPopHost());
        dto.setPopPort(config.getPopPort());
        dto.setImapHost(config.getImapHost());
        dto.setImapPort(config.getImapPort());
        dto.setUseSsl(config.isUseSsl());
        dto.setUseStartTls(config.isUseStartTls());
        dto.setSignatureHtml(config.getSignatureHtml());
        dto.setPasswordSet(config.getSmtpPassword() != null && !config.getSmtpPassword().isBlank());
        return dto;
    }
}
