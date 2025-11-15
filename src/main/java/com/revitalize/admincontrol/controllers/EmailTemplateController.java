package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.EmailTemplateDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.EmailTemplateModel;
import com.revitalize.admincontrol.security.UserAccessService;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.EmailTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/email/templates")
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;
    private final AdmUsuarioService admUsuarioService;
    private final UserAccessService userAccessService;

    public EmailTemplateController(EmailTemplateService emailTemplateService,
                                   AdmUsuarioService admUsuarioService,
                                   UserAccessService userAccessService) {
        this.emailTemplateService = emailTemplateService;
        this.admUsuarioService = admUsuarioService;
        this.userAccessService = userAccessService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmailTemplateModel>> list(@PathVariable("userId") UUID userId,
                                                         @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, userId);
        return ResponseEntity.ok(emailTemplateService.findByUsuario(targetUserId));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid EmailTemplateDto dto,
                                    @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, dto.getUsuarioId());
        Optional<AdmUsuarioModel> usuario = admUsuarioService.findById(targetUserId);
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
    public ResponseEntity<?> update(@PathVariable("id") UUID id,
                                    @RequestBody @Valid EmailTemplateDto dto,
                                    @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        Optional<EmailTemplateModel> templateOpt = emailTemplateService.findById(id);
        if (templateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template não encontrado");
        }
        EmailTemplateModel existing = templateOpt.get();
        userAccessService.assertCanAccessUser(requester, existing.getUsuario().getId());
        existing.setNome(dto.getNome());
        existing.setAssunto(dto.getAssunto());
        existing.setConteudoHtml(dto.getConteudoHtml());
        existing.setUsarAssinatura(dto.getUsarAssinatura() == null || dto.getUsarAssinatura());
        return ResponseEntity.ok(emailTemplateService.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id,
                                    @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        Optional<EmailTemplateModel> templateOpt = emailTemplateService.findById(id);
        if (templateOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template não encontrado");
        }
        EmailTemplateModel existing = templateOpt.get();
        userAccessService.assertCanAccessUser(requester, existing.getUsuario().getId());
        emailTemplateService.delete(existing.getId());
        return ResponseEntity.noContent().build();
    }
}
