package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailTemplateDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.EmailTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/email/templates")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;
    private final AdmUsuarioRepository admUsuarioRepository;

    public EmailTemplateController(EmailTemplateService emailTemplateService,
                                   AdmUsuarioRepository admUsuarioRepository) {
        this.emailTemplateService = emailTemplateService;
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmailTemplateModel>> list(@PathVariable UUID userId) {
        return ResponseEntity.ok(emailTemplateService.findByUsuario(userId));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid EmailTemplateDto dto) {
        Optional<AdmUsuarioModel> usuario = admUsuarioRepository.findById(dto.getUsuarioId());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        EmailTemplateModel template = new EmailTemplateModel();
        BeanUtils.copyProperties(dto, template, "usuarioId");
        template.setUsuario(usuario.get());
        template.setUsarAssinatura(dto.getUsarAssinatura() == null || dto.getUsarAssinatura());
        return ResponseEntity.status(HttpStatus.CREATED).body(emailTemplateService.save(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid EmailTemplateDto dto) {
        return emailTemplateService.findById(id)
                .map(existing -> {
                    if (!existing.getUsuario().getId().equals(dto.getUsuarioId())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Template não pertence a este usuário");
                    }
                    existing.setNome(dto.getNome());
                    existing.setAssunto(dto.getAssunto());
                    existing.setConteudoHtml(dto.getConteudoHtml());
                    existing.setUsarAssinatura(dto.getUsarAssinatura() == null || dto.getUsarAssinatura());
                    return ResponseEntity.ok(emailTemplateService.save(existing));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return emailTemplateService.findById(id)
                .map(existing -> {
                    emailTemplateService.delete(existing.getId());
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template não encontrado"));
    }
}
