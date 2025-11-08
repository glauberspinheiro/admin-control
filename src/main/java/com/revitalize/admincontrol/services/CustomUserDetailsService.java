package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AdmUsuarioRepository;
import com.revitalize.admincontrol.security.EnvironmentAccess;
import com.revitalize.admincontrol.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdmUsuarioRepository admUsuarioRepository;

    public CustomUserDetailsService(AdmUsuarioRepository admUsuarioRepository) {
        this.admUsuarioRepository = admUsuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdmUsuarioModel usuario = admUsuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return buildUserDetails(usuario);
    }

    public UserDetails loadUserById(UUID id) {
        AdmUsuarioModel usuario = admUsuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return buildUserDetails(usuario);
    }

    private UserDetails buildUserDetails(AdmUsuarioModel usuario) {
        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.isActive(),
                true,
                true,
                true,
                buildAuthorities(usuario.getRole(), usuario.getAllowedEnvironments())
        );
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(UserRole role, Set<EnvironmentAccess> envs) {
        Set<EnvironmentAccess> environments = (envs == null || envs.isEmpty())
                ? EnumSet.allOf(EnvironmentAccess.class)
                : envs;
        var authorities = environments.stream()
                .map(env -> new SimpleGrantedAuthority("ENV_" + env.name()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authorities;
    }
}
