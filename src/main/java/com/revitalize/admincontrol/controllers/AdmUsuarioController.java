package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.AdmUsuarioDto;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.security.EnvironmentAccess;
import com.revitalize.admincontrol.security.UserRole;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import com.revitalize.admincontrol.services.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AdmUsuarioController {

    private static final ZoneId SAO_PAULO = ZoneId.of("-03:00");
    private final AdmUsuarioService admUsuarioService;
    private final PasswordService passwordService;

    public AdmUsuarioController(AdmUsuarioService admUsuarioService, PasswordService passwordService) {
        this.admUsuarioService = admUsuarioService;
        this.passwordService = passwordService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<List<AdmUsuarioResponse>> getAll() {
        List<AdmUsuarioResponse> users = admUsuarioService.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<AdmUsuarioResponse> getOne(@PathVariable("id") UUID id) {
        return admUsuarioService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody @Valid AdmUsuarioDto dto) {
        if (admUsuarioService.emailAlreadyInUse(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "E-mail já está em uso"));
        }
        var usuario = new AdmUsuarioModel();
        applyDto(dto, usuario);
        usuario.setDt_cadastro(LocalDateTime.now(SAO_PAULO));
        usuario.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
        return ResponseEntity.status(HttpStatus.CREATED).body(admUsuarioService.saveUsuario(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody @Valid AdmUsuarioDto dto) {
        return admUsuarioService.findById(id)
                .map(existing -> {
                    if (admUsuarioService.emailInUseByOther(dto.getEmail(), existing.getId())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("message", "E-mail já está em uso"));
                    }
                    applyDto(dto, existing);
                    existing.setDt_alteracao_cadastro(LocalDateTime.now(SAO_PAULO));
                    return ResponseEntity.ok(admUsuarioService.saveUsuario(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/recover-password")
    public ResponseEntity<Map<String, String>> recoverPassword(@RequestBody @Valid RecoverPasswordRequest request) {
        return admUsuarioService.findByEmail(request.getEmail())
                .map(user -> ResponseEntity.ok(Map.of("message", "Um e-mail com instruções foi enviado para " + user.getEmail())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "E-mail não encontrado")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        return admUsuarioService.findById(id)
                .map(existing -> {
                    admUsuarioService.deleteById(existing.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class RecoverPasswordRequest {
        @Email
        @NotBlank
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    private void applyDto(AdmUsuarioDto dto, AdmUsuarioModel target) {
        target.setCpf(normalizeCpf(dto.getCpf()));
        target.setNome(dto.getNome());
        target.setEmail(dto.getEmail());
        target.setSenha(passwordService.encode(dto.getSenha()));
        target.setRole(dto.getRole() != null ? dto.getRole() : (target.getRole() == null ? UserRole.OPERATOR : target.getRole()));
        target.setAllowedEnvironments(resolveEnvironments(dto.getEnvironments(), target.getAllowedEnvironments()));
        target.setActive(dto.getActive() != null ? dto.getActive() : (target.isActive()));
    }

    private String normalizeCpf(String cpf) {
        return (cpf == null || cpf.isBlank()) ? null : cpf;
    }

    private Set<EnvironmentAccess> resolveEnvironments(Set<EnvironmentAccess> requested, Set<EnvironmentAccess> current) {
        if (requested == null || requested.isEmpty()) {
            if (current == null || current.isEmpty()) {
                return EnumSet.allOf(EnvironmentAccess.class);
            }
            return current;
        }
        return EnumSet.copyOf(requested);
    }

    private AdmUsuarioResponse toResponse(AdmUsuarioModel model) {
        AdmUsuarioResponse response = new AdmUsuarioResponse();
        response.setId(model.getId());
        response.setCpf(model.getCpf());
        response.setNome(model.getNome());
        response.setEmail(model.getEmail());
        response.setRole(model.getRole());
        response.setEnvironments(model.getAllowedEnvironments());
        response.setActive(model.isActive());
        response.setDtCadastro(model.getDt_cadastro());
        response.setDtAlteracao(model.getDt_alteracao_cadastro());
        return response;
    }

    public static class AdmUsuarioResponse {
        private UUID id;
        private String cpf;
        private String nome;
        private String email;
        private UserRole role;
        private Set<EnvironmentAccess> environments;
        private boolean active;
        private LocalDateTime dtCadastro;
        private LocalDateTime dtAlteracao;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public UserRole getRole() {
            return role;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }

        public Set<EnvironmentAccess> getEnvironments() {
            return environments;
        }

        public void setEnvironments(Set<EnvironmentAccess> environments) {
            this.environments = environments;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public LocalDateTime getDtCadastro() {
            return dtCadastro;
        }

        public void setDtCadastro(LocalDateTime dtCadastro) {
            this.dtCadastro = dtCadastro;
        }

        public LocalDateTime getDtAlteracao() {
            return dtAlteracao;
        }

        public void setDtAlteracao(LocalDateTime dtAlteracao) {
            this.dtAlteracao = dtAlteracao;
        }
    }
}
