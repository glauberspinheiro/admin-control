package com.revitalize.admincontrol.security;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.services.AdmUsuarioService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccessService {

    private final AdmUsuarioService admUsuarioService;

    public UserAccessService(AdmUsuarioService admUsuarioService) {
        this.admUsuarioService = admUsuarioService;
    }

    public AdmUsuarioModel requireCurrentUser(UserDetails principal) {
        if (principal == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return admUsuarioService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não encontrado"));
    }

    public UUID resolveTargetUserId(AdmUsuarioModel requester, UUID requestedId) {
        if (requestedId == null) {
            return requester.getId();
        }
        assertCanAccessUser(requester, requestedId);
        return requestedId;
    }

    public void assertCanAccessUser(AdmUsuarioModel requester, UUID targetUserId) {
        if (targetUserId == null) {
            throw new AccessDeniedException("Usuário alvo inválido");
        }
        if (requester.getId().equals(targetUserId)) {
            return;
        }
        if (!requester.getRole().canManageUsers()) {
            throw new AccessDeniedException("Sem permissão para acessar outro usuário");
        }
    }
}
