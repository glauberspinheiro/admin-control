package com.revitalize.admincontrol.dto;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

public class UserPreferenceDto {

    private UUID usuarioId;
    @NotBlank
    private String theme;
    @NotBlank
    private String language;

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
