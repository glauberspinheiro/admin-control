package com.revitalize.admincontrol.security;

import com.revitalize.admincontrol.models.AccessTokenModel;
import com.revitalize.admincontrol.models.AdmUsuarioModel;
import com.revitalize.admincontrol.repository.AccessTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {

    private static final int TOKEN_BYTES = 48;

    private final AccessTokenRepository accessTokenRepository;
    private final SecurityProperties securityProperties;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenService(AccessTokenRepository accessTokenRepository,
                        SecurityProperties securityProperties,
                        Clock clock) {
        this.accessTokenRepository = accessTokenRepository;
        this.securityProperties = securityProperties;
        this.clock = clock;
    }

    @Transactional
    public TokenView createSessionToken(AdmUsuarioModel usuario) {
        accessTokenRepository.revokeActiveSessions(usuario.getId(), TokenType.SESSION, securityProperties.getEnvironment());

        AccessTokenModel model = new AccessTokenModel();
        model.setUsuario(usuario);
        model.setToken(generateToken());
        model.setType(TokenType.SESSION);
        model.setEnvironment(securityProperties.getEnvironment());
        model.setCreatedAt(LocalDateTime.now(clock));
        model.setPermanent(false);
        model.setExpiresAt(model.getCreatedAt().plus(securityProperties.getSessionTtl()));
        accessTokenRepository.save(model);
        return TokenView.from(model);
    }

    @Transactional
    public TokenView createPermanentApiToken(AdmUsuarioModel usuario, String label) {
        AccessTokenModel model = new AccessTokenModel();
        model.setUsuario(usuario);
        model.setToken(generateToken());
        model.setType(TokenType.API_PERMANENT);
        model.setEnvironment(securityProperties.getEnvironment());
        model.setCreatedAt(LocalDateTime.now(clock));
        model.setPermanent(true);
        model.setExpiresAt(null);
        model.setLabel(label);
        accessTokenRepository.save(model);
        return TokenView.from(model);
    }

    public Optional<AccessTokenModel> findValidToken(String tokenValue) {
        return accessTokenRepository.findByTokenAndRevokedFalse(tokenValue)
                .filter(token -> token.getEnvironment() == securityProperties.getEnvironment())
                .filter(this::isTokenActive);
    }

    @Transactional
    public void revoke(String tokenValue) {
        accessTokenRepository.revokeToken(tokenValue, LocalDateTime.now(clock));
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean isTokenActive(AccessTokenModel token) {
        if (token.isRevoked()) {
            return false;
        }
        if (token.isPermanent()) {
            return true;
        }
        LocalDateTime expiresAt = token.getExpiresAt();
        return expiresAt == null || expiresAt.isAfter(LocalDateTime.now(clock));
    }

    public record TokenView(String value,
                            TokenType type,
                            EnvironmentAccess environment,
                            boolean permanent,
                            LocalDateTime expiresAt,
                            LocalDateTime createdAt,
                            String label) {
        public static TokenView from(AccessTokenModel model) {
            return new TokenView(
                    model.getToken(),
                    model.getType(),
                    model.getEnvironment(),
                    model.isPermanent(),
                    model.getExpiresAt(),
                    model.getCreatedAt(),
                    model.getLabel()
            );
        }
    }
}
