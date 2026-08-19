package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.DashboardResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // =========================================================
    // GET DASHBOARD SUMMARY
    // =========================================================

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboardSummary() {

        DashboardResponseDTO responseDTO = dashboardService.getDashboardSummary();

        return ResponseEntity.ok(responseDTO);
    }
}