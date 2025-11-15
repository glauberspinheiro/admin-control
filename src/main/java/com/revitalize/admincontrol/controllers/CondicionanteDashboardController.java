package com.revitalize.admincontrol.controllers;

import com.revitalize.admincontrol.dto.condicionantes.CondicionanteDashboardDto;
import com.revitalize.admincontrol.services.CondicionanteDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/condicionantes/dashboard")
public class CondicionanteDashboardController {

    private final CondicionanteDashboardService dashboardService;

    public CondicionanteDashboardController(CondicionanteDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public CondicionanteDashboardDto dashboard() {
        return dashboardService.construirDashboard();
    }
}
