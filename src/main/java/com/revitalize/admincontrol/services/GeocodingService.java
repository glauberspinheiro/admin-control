package com.revitalize.admincontrol.services;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.repository.AdmEmpresaGeoRepository;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.URI;
import java.util.*;

@Service
public class GeocodingService {
    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    private final AdmEmpresaGeoRepository repo;
    private final RestTemplate http;

    private static final String USER_AGENT = "admin-control-dev/0.1 (contato: glauberspinheiro@gmail.com)";
    private static final String NOMINATIM = "https://nominatim.openstreetmap.org/search";

    public GeocodingService(AdmEmpresaGeoRepository repo) {
        this.repo = repo;
        // RestTemplate com timeouts
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(5000);
        rf.setReadTimeout(8000);
        this.http = new RestTemplate(rf);
    }

    @Transactional
    public Optional<AdmEmpresaModel> geocodeAndSave(UUID companyId) {
        Optional<AdmEmpresaModel> opt = repo.findById(companyId);
        if (opt.isEmpty()) return Optional.empty();

        AdmEmpresaModel emp = opt.get();

        // Idempotente: se já tem lat/lng, não chama serviço externo
        if (emp.getLat() != null && emp.getLng() != null) {
            log.info("[GEO] Empresa {} já possui lat/lng: {}, {}", companyId, emp.getLat(), emp.getLng());
            return Optional.of(emp);
        }

        String q = buildQuery(emp);
        if (q.isBlank()) {
            throw new IllegalArgumentException("Endereço insuficiente (logradouro/numero/bairro/municipio/UF/CEP)");
        }

        Map<String,Object> result = queryNominatim(q);
        if (result == null) {
            throw new IllegalStateException("Endereço não encontrado no geocoder (Nominatim).");
        }

        Double lat = parseDouble(result.get("lat"));
        Double lon = parseDouble(result.get("lon"));
        if (lat == null || lon == null) {
            throw new IllegalStateException("Resposta do geocoder sem lat/lon.");
        }

        emp.setLat(lat);
        emp.setLng(lon);
        repo.save(emp);

        log.info("[GEO] {} => lat={}, lng={}", safeName(emp), lat, lon);
        return Optional.of(emp);
    }

    @Transactional
    public int geocodeMissing(int limit, long delayMs) {
        var page = repo.findByLatIsNullOrLngIsNull(org.springframework.data.domain.PageRequest.of(0, Math.max(1, limit)));
        int count = 0;
        for (AdmEmpresaModel emp : page.getContent()) {
            try {
                geocodeAndSave(emp.getId());
                count++;
                if (delayMs > 0) Thread.sleep(delayMs); // respeita ~1 req/s do Nominatim
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception ex) {
                log.warn("[GEO] erro empresa {}: {}", emp.getId(), ex.getMessage());
            }
        }
        return count;
    }

    // ---------------- helpers ----------------

    private Map<String,Object> queryNominatim(String query) {
        // IMPORTANTE: usar build().encode() para encodar espaços e caracteres especiais
        URI uri = UriComponentsBuilder.fromHttpUrl(NOMINATIM)
                .queryParam("format", "json")
                .queryParam("addressdetails", "0")
                .queryParam("limit", "1")
                .queryParam("countrycodes", "br")
                .queryParam("q", query)
                .build()
                .encode()   // <-- corrige o erro "Invalid character ' ' for QUERY_PARAM"
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> req = new HttpEntity<>(headers);

        log.info("[GEO] GET {}", uri);
        try {
            ResponseEntity<List<Map<String,Object>>> resp = http.exchange(
                    uri, HttpMethod.GET, req, new ParameterizedTypeReference<List<Map<String,Object>>>() {}
            );
            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.warn("[GEO] HTTP {} na consulta", resp.getStatusCodeValue());
                return null;
            }
            List<Map<String,Object>> body = resp.getBody();
            if (body == null || body.isEmpty()) {
                log.warn("[GEO] body vazio para '{}'", query);
                return null;
            }
            return body.get(0);
        } catch (HttpStatusCodeException e) {
            String payload = e.getResponseBodyAsString();
            log.error("[GEO] {} ao chamar geocoder. Body: {}", e.getStatusCode(), payload);
            throw new RuntimeException("Falha no geocoder: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("[GEO] erro inesperado ao chamar geocoder", e);
            throw new RuntimeException("Falha ao chamar geocoder: " + e.getMessage(), e);
        }
    }

    private String buildQuery(AdmEmpresaModel e) {
        List<String> parts = new ArrayList<>();
        safeAdd(parts, e.getLogradouro());
        safeAdd(parts, e.getNumero());
        safeAdd(parts, e.getBairro());
        safeAdd(parts, e.getMunicipio());
        safeAdd(parts, e.getUf());
        safeAdd(parts, e.getCep());
        parts.add("Brasil");
        return String.join(", ", parts);
    }

    private void safeAdd(List<String> list, String v) {
        if (v != null && !v.isBlank()) list.add(v.trim());
    }

    private Double parseDouble(Object o) {
        try { return o == null ? null : Double.parseDouble(String.valueOf(o)); }
        catch (Exception ignored) { return null; }
    }

    private String safeName(AdmEmpresaModel e) {
        try {
            if (e.getNomeEmpresa() != null && !e.getNomeEmpresa().isBlank()) return e.getNomeEmpresa();
            if (e.getNomeFantasia() != null && !e.getNomeFantasia().isBlank()) return e.getNomeFantasia();
        } catch (Exception ignored) {}
        return "Empresa";
    }
}
