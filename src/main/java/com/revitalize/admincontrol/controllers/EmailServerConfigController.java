package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailServerConfigDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailServerConfigModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/email/server-config")
public class EmailServerConfigController {

    private final EmailServerConfigService emailServerConfigService;
    private final AdmUsuarioRepository admUsuarioRepository;

    public EmailServerConfigController(EmailServerConfigService emailServerConfigService,
                                       AdmUsuarioRepository admUsuarioRepository) {
        this.emailServerConfigService = emailServerConfigService;
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable UUID userId) {
        return emailServerConfigService.findByUsuarioId(userId)
                .map(config -> ResponseEntity.ok(mapToDto(config)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid EmailServerConfigDto dto) {
        AdmUsuarioModel usuario = admUsuarioRepository.findById(dto.getUsuarioId())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        EmailServerConfigModel config = emailServerConfigService.findByUsuarioId(dto.getUsuarioId())
                .orElse(new EmailServerConfigModel());
        BeanUtils.copyProperties(dto, config, "usuarioId");
        config.setUsuario(usuario);
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
        return dto;
    }
}
