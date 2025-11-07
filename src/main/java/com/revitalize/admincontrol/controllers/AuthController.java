package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.UserPreferenceModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import com.revitalize.admincontrol.services.UserPreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final AdmUsuarioRepository admUsuarioRepository;
    private final UserPreferenceService userPreferenceService;
    private final EmailServerConfigService emailServerConfigService;

    public AuthController(AdmUsuarioRepository admUsuarioRepository,
                          UserPreferenceService userPreferenceService,
                          EmailServerConfigService emailServerConfigService) {
        this.admUsuarioRepository = admUsuarioRepository;
        this.userPreferenceService = userPreferenceService;
        this.emailServerConfigService = emailServerConfigService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        AdmUsuarioModel usuario = admUsuarioRepository.Login(request.getEmail(), request.getSenha());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário ou senha inválidos"));
        }
        UserPreferenceModel preference = userPreferenceService.getOrCreate(usuario);
        boolean hasEmailConfig = emailServerConfigService.hasConfig(usuario.getId());
        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                preference.getTheme(),
                preference.getLanguage(),
                hasEmailConfig
        ));
    }

    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String senha;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }

    public static class LoginResponse {
        private UUID id;
        private String nome;
        private String email;
        private String theme;
        private String language;
        private boolean hasEmailConfig;

        public LoginResponse(UUID id, String nome, String email, String theme, String language, boolean hasEmailConfig) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.theme = theme;
            this.language = language;
            this.hasEmailConfig = hasEmailConfig;
        }

        public UUID getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getEmail() {
            return email;
        }

        public String getTheme() {
            return theme;
        }

        public String getLanguage() {
            return language;
        }

        public boolean isHasEmailConfig() {
            return hasEmailConfig;
        }
    }
}
