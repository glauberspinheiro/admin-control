package com.revitalize.admincontrol.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    /**
     * Indicates which environment (DEV/STAGING/PROD) this instance represents.
     */
    private EnvironmentAccess environment = EnvironmentAccess.DEV;

    /**
     * Default time-to-live for session tokens.
     */
    private Duration sessionTtl = Duration.ofHours(2);

    public EnvironmentAccess getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentAccess environment) {
        this.environment = environment;
    }

    public Duration getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(Duration sessionTtl) {
        this.sessionTtl = sessionTtl;
    }
}
