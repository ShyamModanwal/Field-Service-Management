package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.TimeLogRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.TimeLogResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.TimeLogService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class TimeLogController {

    private final TimeLogService timeLogService;

    public TimeLogController(TimeLogService timeLogService) {
        this.timeLogService = timeLogService;
    }

    // =========================================================
    // CREATE TIME LOG
    // =========================================================

    @PostMapping("/{workOrderId}/time-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<TimeLogResponseDTO> createTimeLog(
            @PathVariable Long workOrderId,
            @RequestBody TimeLogRequestDTO requestDTO) {

        TimeLogResponseDTO responseDTO = timeLogService.createTimeLog(
                workOrderId,
                requestDTO);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.CREATED);
    }

    // =========================================================
    // GET TIME LOGS BY WORK ORDER
    // =========================================================

    @GetMapping("/{workOrderId}/time-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<TimeLogResponseDTO>> getTimeLogsByWorkOrder(
            @PathVariable Long workOrderId) {

        return ResponseEntity.ok(
                timeLogService
                        .getTimeLogsByWorkOrder(workOrderId));
    }

    // =========================================================
    // GET TIME LOGS BY TECHNICIAN
    // =========================================================

    @GetMapping("/time-logs/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<TimeLogResponseDTO>> getTimeLogsByTechnician(
            @PathVariable Long technicianId) {

        return ResponseEntity.ok(
                timeLogService
                        .getTimeLogsByTechnician(technicianId));
    }
}