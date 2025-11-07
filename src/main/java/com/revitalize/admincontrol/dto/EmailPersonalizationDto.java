package com.revitalize.admincontrol.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Map;

public class EmailPersonalizationDto {

    @Email
    @NotBlank
    private String destinatario;

    private Map<String, String> variaveis;

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Map<String, String> getVariaveis() {
        return variaveis;
    }

    public void setVariaveis(Map<String, String> variaveis) {
        this.variaveis = variaveis;
    }
}
