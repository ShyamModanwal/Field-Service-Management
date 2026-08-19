package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.service.SLAService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-orders")
public class SLAController {

    private final SLAService slaService;

    public SLAController(SLAService slaService) {
        this.slaService = slaService;
    }

    // =========================================================
    // CALCULATE / SET SLA
    // =========================================================

    @PostMapping("/{workOrderId}/sla")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkOrder> calculateSla(
            @PathVariable Long workOrderId) {

        WorkOrder workOrder = slaService.calculateSla(workOrderId);

        return ResponseEntity.ok(workOrder);
    }

    // =========================================================
    // CHECK SLA STATUS
    // =========================================================

    @GetMapping("/{workOrderId}/sla")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<String> getSlaStatus(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                slaService.getSlaStatus(workOrderId));
    }
}