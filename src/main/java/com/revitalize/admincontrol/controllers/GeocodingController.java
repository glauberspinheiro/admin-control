package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.services.GeocodingService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/companies")
public class GeocodingController {
    private static final Logger log = LoggerFactory.getLogger(GeocodingController.class);
    private final GeocodingService geo;

    public GeocodingController(GeocodingService geo) {
        this.geo = geo;
    }

    /**
     * Geocodifica UMA empresa.
     * Requer: ROLE_SUPER_ADMIN ou ROLE_ADMIN ou ROLE_API ou ROLE_OPERATOR
     * + alguma autoridade de ambiente (ENV_DEV/ENV_STAGING/ENV_PROD).
     */
    @PostMapping("/{id}/geocode")
    @PreAuthorize("(hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('API') or hasRole('OPERATOR')) and " +
                  "(hasAuthority('ENV_DEV') or hasAuthority('ENV_STAGING') or hasAuthority('ENV_PROD'))")
    public ResponseEntity<AdmEmpresaModel> geocodeOne(@PathVariable("id") UUID id) {
        Optional<AdmEmpresaModel> updated = geo.geocodeAndSave(id);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Geocodifica N empresas sem lat/lng (batch helper).
     * Requer: ROLE_SUPER_ADMIN ou ROLE_ADMIN
     * + autoridade de environment.
     */
    @PostMapping("/geocode-missing")
    @PreAuthorize("(hasRole('SUPER_ADMIN') or hasRole('ADMIN')) and " +
                  "(hasAuthority('ENV_DEV') or hasAuthority('ENV_STAGING') or hasAuthority('ENV_PROD'))")
    public ResponseEntity<String> geocodeMissing(@RequestParam(defaultValue = "10") int limit,
                                                 @RequestParam(defaultValue = "1200") long delayMs) {
        int done = geo.geocodeMissing(limit, delayMs);
        return ResponseEntity.ok("Geocodificadas: " + done);
    }
}
