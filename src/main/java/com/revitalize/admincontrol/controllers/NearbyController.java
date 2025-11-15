package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.NearbyDTO;
import com.revitalize.admincontrol.services.NearbyService;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/map/nearby")
public class NearbyController {
    private static final Logger log = LoggerFactory.getLogger(NearbyController.class);
    private final NearbyService service;

    public NearbyController(NearbyService s) { this.service = s; }

    @GetMapping
    public NearbyDTO nearby(@RequestParam("companyId") UUID companyId,
                            @RequestParam(value = "tipo", required = false) String tipo,
                            @RequestParam(value = "limit", required = false) Integer limit) {
        try { return service.findNearest(companyId, tipo, limit); }
        catch (Exception e) { log.error("[NEARBY] failed", e); throw e; }
    }
}
