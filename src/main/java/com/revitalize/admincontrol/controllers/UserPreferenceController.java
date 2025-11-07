package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.UserPreferenceDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.UserPreferenceModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.UserPreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user/preferences")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final AdmUsuarioRepository admUsuarioRepository;

    public UserPreferenceController(UserPreferenceService userPreferenceService,
                                    AdmUsuarioRepository admUsuarioRepository) {
        this.userPreferenceService = userPreferenceService;
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(@PathVariable("userId") UUID userId) {
        return userPreferenceService.findByUsuarioId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid UserPreferenceDto dto) {
        AdmUsuarioModel usuario = admUsuarioRepository.findById(dto.getUsuarioId())
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
