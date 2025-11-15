package com.revitalize.admincontrol.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revitalize.admincontrol.models.PrestadorModel;
import com.revitalize.admincontrol.repository.PrestadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PrestadorImportService {
    private static final Logger log = LoggerFactory.getLogger(PrestadorImportService.class);

    private static final String NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    private final PrestadorRepository prestadorRepository;
    private final RestTemplate http;
    private final String apiKey;
    private final String language;
    private final int defaultRadiusMeters;

    public PrestadorImportService(PrestadorRepository prestadorRepository,
                                  RestTemplateBuilder builder,
                                  @Value("${app.map.google.api-key:}") String apiKey,
                                  @Value("${app.map.google.language:pt-BR}") String language,
                                  @Value("${app.map.google.radius-meters:10000}") int defaultRadiusMeters) {
        this.prestadorRepository = prestadorRepository;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.language = (language == null || language.isBlank()) ? "pt-BR" : language;
        this.defaultRadiusMeters = defaultRadiusMeters > 0 ? defaultRadiusMeters : 10000;
        this.http = builder
                .setConnectTimeout(Duration.ofSeconds(6))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Transactional
    public List<PrestadorModel> syncProviders(double lat, double lng, String tipo, int radiusMeters, int limit) {
        if (!StringUtils.hasText(apiKey)) {
            log.warn("[PLACES] GOOGLE_PLACES_API_KEY não configurada. Busca online ignorada.");
            return Collections.emptyList();
        }

        int effectiveRadius = radiusMeters > 0 ? radiusMeters : defaultRadiusMeters;
        int cappedLimit = limit <= 0 ? 5 : Math.min(limit, 10);
        URI uri = UriComponentsBuilder.fromHttpUrl(NEARBY_URL)
                .queryParam("location", lat + "," + lng)
                .queryParam("radius", effectiveRadius)
                .queryParam("keyword", resolveKeyword(tipo))
                .queryParam("language", language)
                .queryParam("key", apiKey)
                .build(true)
                .toUri();

        try {
            log.info("[PLACES] GET {} (tipo={}, limit={})", uri, tipo, cappedLimit);
            PlacesResponse response = http.getForObject(uri, PlacesResponse.class);
            if (response == null) {
                log.warn("[PLACES] Resposta vazia do Google Places.");
                return Collections.emptyList();
            }

            log.info("[PLACES] status={} nextPage={}", response.status, response.nextPageToken);
            if (!"OK".equalsIgnoreCase(response.status) && !"ZERO_RESULTS".equalsIgnoreCase(response.status)) {
                log.warn("[PLACES] status inesperado: {} msg={}", response.status, response.errorMessage);
                return Collections.emptyList();
            }

            if (response.results == null || response.results.isEmpty()) {
                log.info("[PLACES] Nenhum resultado encontrado para lat={}, lng={}", lat, lng);
                return Collections.emptyList();
            }
            List<PrestadorModel> imported = new ArrayList<>();
            for (PlaceResult place : response.results) {
                PrestadorModel saved = upsertPlace(place, tipo);
                if (saved != null) {
                    imported.add(saved);
                    log.debug("[PLACES] salvo {} ({}) lat={},lng={}", saved.getNome(), saved.getTipo(), saved.getLat(), saved.getLng());
                }
                if (imported.size() >= cappedLimit) break;
            }
            log.info("[PLACES] Importados {} prestadores online.", imported.size());
            return imported;
        } catch (RestClientException ex) {
            log.error("[PLACES] Falha ao consultar Google Places", ex);
            return Collections.emptyList();
        }
    }

    private PrestadorModel upsertPlace(PlaceResult place, String tipo) {
        if (place == null || place.geometry == null || place.geometry.location == null) {
            return null;
        }
        String resolvedType = resolveTipo(tipo);
        String name = (place.name == null || place.name.isBlank()) ? "Prestador" : place.name.trim();
        Location loc = place.geometry.location;

        Optional<PrestadorModel> existing = prestadorRepository.findFirstByNomeIgnoreCaseAndTipo(name, resolvedType);
        PrestadorModel entity = existing.orElseGet(PrestadorModel::new);
        entity.setNome(name);
        entity.setTipo(resolvedType);
        entity.setLat(loc.lat);
        entity.setLng(loc.lng);

        PlaceDetails details = fetchDetails(place.placeId);
        if (details != null) {
            entity.setTelefone(details.formattedPhoneNumber);
            entity.setSite(details.website);
        }

        return prestadorRepository.save(entity);
    }

    private PlaceDetails fetchDetails(String placeId) {
        if (!StringUtils.hasText(placeId)) return null;
        URI uri = UriComponentsBuilder.fromHttpUrl(DETAILS_URL)
                .queryParam("place_id", placeId)
                .queryParam("fields", "formatted_phone_number,website")
                .queryParam("language", language)
                .queryParam("key", apiKey)
                .build(true)
                .toUri();
        try {
            PlaceDetailsResponse resp = http.getForObject(uri, PlaceDetailsResponse.class);
            return resp != null ? resp.result : null;
        } catch (Exception ex) {
            log.debug("[PLACES] Falha ao buscar detalhes de {}: {}", placeId, ex.getMessage());
            return null;
        }
    }

    private String resolveKeyword(String tipo) {
        if ("transporte_residuo".equalsIgnoreCase(tipo)) return "transporte de resíduo";
        if ("logistica_reversa".equalsIgnoreCase(tipo)) return "logística reversa";
        return "gestão de resíduos";
    }

    private String resolveTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) return "logistica_reversa";
        return tipo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PlacesResponse {
        public List<PlaceResult> results;
        public String status;
        @JsonProperty("error_message")
        public String errorMessage;
        @JsonProperty("next_page_token")
        public String nextPageToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PlaceResult {
        @JsonProperty("place_id")
        public String placeId;
        public String name;
        public Geometry geometry;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Geometry {
        public Location location;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Location {
        public Double lat;
        public Double lng;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PlaceDetailsResponse {
        public PlaceDetails result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class PlaceDetails {
        @JsonProperty("formatted_phone_number")
        public String formattedPhoneNumber;
        public String website;
    }
}
