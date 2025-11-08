package com.revitalize.admincontrol.repository;

import com.revitalize.admincontrol.models.AccessTokenModel;
import com.revitalize.admincontrol.security.EnvironmentAccess;
import com.revitalize.admincontrol.security.TokenType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessTokenModel, UUID> {

    Optional<AccessTokenModel> findByTokenAndRevokedFalse(String token);

    @Modifying
    @Query("update AccessTokenModel t set t.revoked = true where t.usuario.id = :userId and t.type = :type and t.environment = :environment and t.revoked = false")
    void revokeActiveSessions(@Param("userId") UUID userId,
                              @Param("type") TokenType type,
                              @Param("environment") EnvironmentAccess environment);

    @Modifying
    @Query("update AccessTokenModel t set t.revoked = true, t.expiresAt = :revokedAt where t.token = :token")
    void revokeToken(@Param("token") String token, @Param("revokedAt") LocalDateTime revokedAt);
}
