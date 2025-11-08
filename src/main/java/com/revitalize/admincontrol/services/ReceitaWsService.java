package com.revitalize.admincontrol.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revitalize.admincontrol.dto.AdmEmpresaDto;
import com.revitalize.admincontrol.utils.CnpjUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReceitaWsService {

    private static final String RECEITA_WS_ENDPOINT = "https://receitaws.com.br/v1/cnpj/{cnpj}";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ReceitaWsService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = objectMapper;
    }

    public AdmEmpresaDto lookupCompany(String cnpj) {
        String digits = CnpjUtils.onlyDigits(cnpj);
        if (!CnpjUtils.isValid(digits)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CNPJ inválido.");
        }
        try {
            String payload = restTemplate.getForObject(RECEITA_WS_ENDPOINT, String.class, digits);
            JsonNode root = objectMapper.readTree(payload);
            if (!"OK".equalsIgnoreCase(root.path("status").asText())) {
                String message = root.path("message").asText("CNPJ não encontrado.");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
            }

            AdmEmpresaDto dto = new AdmEmpresaDto();
            dto.setCnpj(digits);
            dto.setNomeempresa(root.path("nome").asText(""));
            dto.setNomefantasia(nullIfBlank(root.path("fantasia").asText("")));
            dto.setEmail(nullIfBlank(root.path("email").asText("")));
            dto.setTelefone(limitLength(onlyDigits(root.path("telefone").asText("")), 20));
            dto.setContato(extractFirstPartnerName(root));
            dto.setMensalista("N");
            dto.setStatus("A");
            dto.setCep(limitLength(onlyDigits(root.path("cep").asText("")), 8));
            dto.setLogradouro(nullIfBlank(root.path("logradouro").asText("")));
            dto.setNumero(nullIfBlank(root.path("numero").asText("")));
            dto.setComplemento(nullIfBlank(root.path("complemento").asText("")));
            dto.setBairro(nullIfBlank(root.path("bairro").asText("")));
            dto.setMunicipio(nullIfBlank(root.path("municipio").asText("")));
            dto.setUf(limitLength(upper(nullIfBlank(root.path("uf").asText(""))), 2));
            dto.setRegimeTributario(determineRegime(root));
            dto.setAtividadePrincipal(extractMainActivity(root));
            dto.setAtividadesSecundarias(extractSecondaryActivities(root));
            dto.setSocios(extractPartners(root));
            return dto;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Limite de consultas à ReceitaWS atingido. Tente novamente em instantes.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A ReceitaWS retornou um erro: " + ex.getStatusCode().value());
        } catch (RestClientException | IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível consultar a ReceitaWS.", ex);
        }
    }

    private String extractFirstPartnerName(JsonNode root) {
        JsonNode partners = root.path("qsa");
        if (partners.isArray() && partners.size() > 0) {
            return nullIfBlank(partners.get(0).path("nome").asText(""));
        }
        return null;
    }

    private String extractMainActivity(JsonNode root) {
        JsonNode activityArray = root.path("atividade_principal");
        if (activityArray.isArray() && activityArray.size() > 0) {
            return formatActivity(activityArray.get(0));
        }
        return null;
    }

    private String extractSecondaryActivities(JsonNode root) {
        JsonNode activityArray = root.path("atividades_secundarias");
        if (activityArray.isArray() && activityArray.size() > 0) {
            return StreamSupport.stream(activityArray.spliterator(), false)
                    .map(this::formatActivity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));
        }
        return null;
    }

    private String formatActivity(JsonNode node) {
        if (node == null) {
            return null;
        }
        String code = nullIfBlank(node.path("code").asText(""));
        String text = nullIfBlank(node.path("text").asText(""));
        if (isBlank(code) && isBlank(text)) {
            return null;
        }
        if (isBlank(code)) {
            return text;
        }
        if (isBlank(text)) {
            return code;
        }
        return code + " - " + text;
    }

    private String extractPartners(JsonNode root) {
        JsonNode partners = root.path("qsa");
        if (partners.isArray() && partners.size() > 0) {
            return StreamSupport.stream(partners.spliterator(), false)
                    .map(this::formatPartner)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("\n"));
        }
        return null;
    }

    private String formatPartner(JsonNode node) {
        if (node == null) {
            return null;
        }
        String nome = nullIfBlank(node.path("nome").asText(""));
        if (isBlank(nome)) {
            return null;
        }
        String qual = nullIfBlank(node.path("qual").asText(""));
        return isBlank(qual) ? nome : nome + " - " + qual;
    }

    private String determineRegime(JsonNode root) {
        JsonNode simples = root.path("simples");
        JsonNode simei = root.path("simei");
        boolean optanteSimples = isAffirmative(simples.path("optante").asText())
                || isAffirmative(simples.path("simples").asText());
        boolean optanteMei = isAffirmative(simei.path("optante").asText())
                || isAffirmative(simples.path("mei").asText());
        if (optanteSimples) {
            return "Simples Nacional";
        }
        if (optanteMei) {
            return "MEI";
        }
        return "Lucro presumido";
    }

    private boolean isAffirmative(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.equals("sim") || normalized.equals("s");
    }

    private String limitLength(String value, int max) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    private String nullIfBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String onlyDigits(String value) {
        return CnpjUtils.onlyDigits(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String upper(String value) {
        return value == null ? null : value.toUpperCase();
    }
}
