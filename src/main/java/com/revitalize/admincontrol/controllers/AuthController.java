package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.models.UserPreferenceModel;
import com.revitalize.admincontrol.security.EnvironmentAccess;
import com.revitalize.admincontrol.security.SecurityProperties;
import com.revitalize.admincontrol.security.TokenService;
import com.revitalize.admincontrol.security.TokenService.TokenView;
import com.revitalize.admincontrol.security.UserRole;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.EmailServerConfigService;
import com.revitalize.admincontrol.services.PasswordService;
import com.revitalize.admincontrol.services.UserPreferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final AdmUsuarioService admUsuarioService;
    private final UserPreferenceService userPreferenceService;
    private final EmailServerConfigService emailServerConfigService;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final SecurityProperties securityProperties;

    public AuthController(AdmUsuarioService admUsuarioService,
                          UserPreferenceService userPreferenceService,
                          EmailServerConfigService emailServerConfigService,
                          PasswordService passwordService,
                          TokenService tokenService,
                          SecurityProperties securityProperties) {
        this.admUsuarioService = admUsuarioService;
        this.userPreferenceService = userPreferenceService;
        this.emailServerConfigService = emailServerConfigService;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.securityProperties = securityProperties;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Optional<AdmUsuarioModel> usuarioOpt = admUsuarioService.findByEmail(request.getEmail());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário ou senha inválidos"));
        }
        AdmUsuarioModel usuario = usuarioOpt.get();
        if (!passwordService.matches(usuario, request.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário ou senha inválidos"));
        }
        if (!usuario.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Usuário desativado"));
        }
        EnvironmentAccess targetEnvironment = securityProperties.getEnvironment();
        if (usuario.getAllowedEnvironments() == null || !usuario.getAllowedEnvironments().contains(targetEnvironment)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Usuário sem permissão para o ambiente " + targetEnvironment.name()));
        }

        UserPreferenceModel preference = userPreferenceService.getOrCreate(usuario);
        boolean hasEmailConfig = emailServerConfigService.hasConfig(usuario.getId());
        TokenView token = tokenService.createSessionToken(usuario);

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                targetEnvironment,
                preference.getTheme(),
                preference.getLanguage(),
                hasEmailConfig,
                TokenPayload.from(token)
        ));
    }

    @PostMapping("/token/permanent")
    @PreAuthorize("hasRole('API')")
    public ResponseEntity<?> createPermanentToken(@AuthenticationPrincipal UserDetails principal,
                                                  @RequestBody @Valid PermanentTokenRequest request) {
        AdmUsuarioModel usuario = admUsuarioService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

        if (!usuario.getRole().isApiClient()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Perfil não autorizado para token permanente"));
        }

        TokenView token = tokenService.createPermanentApiToken(usuario, request.getLabel());
        return ResponseEntity.status(HttpStatus.CREATED).body(TokenPayload.from(token));
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
         private UserRole role;
         private EnvironmentAccess environment;
        private String theme;
        private String language;
        private boolean hasEmailConfig;
         private TokenPayload token;

        public LoginResponse(UUID id,
                             String nome,
                             String email,
                             UserRole role,
                             EnvironmentAccess environment,
                             String theme,
                             String language,
                             boolean hasEmailConfig,
                             TokenPayload token) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.role = role;
            this.environment = environment;
            this.theme = theme;
            this.language = language;
            this.hasEmailConfig = hasEmailConfig;
            this.token = token;
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

        public UserRole getRole() {
            return role;
        }

        public EnvironmentAccess getEnvironment() {
            return environment;
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

        public TokenPayload getToken() {
            return token;
        }
    }

    public static class TokenPayload {
        private String value;
        private String type;
        private EnvironmentAccess environment;
        private boolean permanent;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;
        private String label;

        public static TokenPayload from(TokenView view) {
            TokenPayload payload = new TokenPayload();
            payload.value = view.value();
            payload.type = view.type().name();
            payload.environment = view.environment();
            payload.permanent = view.permanent();
            payload.expiresAt = view.expiresAt();
            payload.createdAt = view.createdAt();
            payload.label = view.label();
            return payload;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public EnvironmentAccess getEnvironment() {
            return environment;
        }

        public boolean isPermanent() {
            return permanent;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class PermanentTokenRequest {
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
