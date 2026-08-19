package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.PartUsageRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.PartUsageResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.PartUsageService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class PartUsageController {

    private final PartUsageService partUsageService;

    public PartUsageController(
            PartUsageService partUsageService) {

        this.partUsageService = partUsageService;
    }

    // =========================================================
    // USE PART ON WORK ORDER
    // =========================================================

    @PostMapping("/{workOrderId}/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<PartUsageResponseDTO> usePart(
            @PathVariable Long workOrderId,
            @RequestBody PartUsageRequestDTO requestDTO) {

        PartUsageResponseDTO responseDTO = partUsageService.usePart(
                workOrderId,
                requestDTO);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.CREATED);
    }

    // =========================================================
    // GET PARTS USED BY WORK ORDER
    // =========================================================

    @GetMapping("/{workOrderId}/parts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<PartUsageResponseDTO>> getPartsUsedByWorkOrder(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                partUsageService
                        .getPartsUsedByWorkOrder(workOrderId));
    }
}