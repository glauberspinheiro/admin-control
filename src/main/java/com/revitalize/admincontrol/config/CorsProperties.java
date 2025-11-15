package com.revitalize.admincontrol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Lista de origens permitidas para requisições via CORS.
     * Use "*" para liberar geral apenas em ambientes de desenvolvimento.
     */
    private List<String> allowedOrigins = new ArrayList<>(List.of("*"));

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
