package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final AdmUsuarioRepository admUsuarioRepository;

    public PasswordService(PasswordEncoder passwordEncoder, AdmUsuarioRepository admUsuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.admUsuarioRepository = admUsuarioRepository;
    }

    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Transactional
    public boolean matches(AdmUsuarioModel usuario, String rawPassword) {
        String stored = usuario.getSenha();
        if (stored == null) {
            return false;
        }
        if (passwordEncoder.matches(rawPassword, stored)) {
            return true;
        }
        if (!stored.startsWith("{") && stored.equals(rawPassword)) {
            usuario.setSenha(passwordEncoder.encode(rawPassword));
            admUsuarioRepository.save(usuario);
            return true;
        }
        return false;
    }
}
