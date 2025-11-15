package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.UserPreferenceDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.UserPreferenceModel;
import com.revitalize.admincontrol.security.UserAccessService;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.UserPreferenceService;
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
@RequestMapping("/api/user/preferences")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final AdmUsuarioService admUsuarioService;
    private final UserAccessService userAccessService;

    public UserPreferenceController(UserPreferenceService userPreferenceService,
                                    AdmUsuarioService admUsuarioService,
                                    UserAccessService userAccessService) {
        this.userPreferenceService = userPreferenceService;
        this.admUsuarioService = admUsuarioService;
        this.userAccessService = userAccessService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") UUID userId,
                                 @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, userId);
        return userPreferenceService.findByUsuarioId(targetUserId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid UserPreferenceDto dto,
                                  @AuthenticationPrincipal UserDetails principal) {
        AdmUsuarioModel requester = userAccessService.requireCurrentUser(principal);
        UUID targetUserId = userAccessService.resolveTargetUserId(requester, dto.getUsuarioId());
        AdmUsuarioModel usuario = admUsuarioService.findById(targetUserId)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        UserPreferenceModel preference = userPreferenceService.getOrCreate(usuario);
        preference.setTheme(dto.getTheme());
        preference.setLanguage(dto.getLanguage());
        return ResponseEntity.ok(userPreferenceService.save(preference));
    }
}
