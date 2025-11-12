package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.models.AdmEmpresaModel;
import com.revitalize.admincontrol.services.GeocodingService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

    @PostMapping("/{id}/geocode")
    @PreAuthorize("(hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('API') or hasRole('OPERATOR')) and " +
                  "(hasAuthority('ENV_DEV') or hasAuthority('ENV_STAGING') or hasAuthority('ENV_PROD'))")
    public ResponseEntity<AdmEmpresaModel> geocodeOne(@PathVariable("id") UUID id,
                                                      @RequestParam(value = "force", defaultValue = "false") boolean force) {
        Optional<AdmEmpresaModel> updated = geo.geocodeAndSave(id, force);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class CoordDTO { public Double lat; public Double lng; }

    @PutMapping("/{id}/coords")
    @PreAuthorize("(hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('API') or hasRole('OPERATOR')) and " +
                  "(hasAuthority('ENV_DEV') or hasAuthority('ENV_STAGING') or hasAuthority('ENV_PROD'))")
    public ResponseEntity<?> setCoords(@PathVariable("id") UUID id, @RequestBody CoordDTO dto) {
        if (dto == null || dto.lat == null || dto.lng == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "lat e lng são obrigatórios."));
        }
        return geo.updateCoords(id, dto.lat, dto.lng)
                .<ResponseEntity<?>>map(e -> ResponseEntity.ok(Map.of(
                        "message", "Coordenadas atualizadas com sucesso.",
                        "id", e.getId(),
                        "lat", e.getLat(),
                        "lng", e.getLng()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
