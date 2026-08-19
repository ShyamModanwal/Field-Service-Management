package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.PartRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.PartResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.PartService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    // =========================================================
    // CREATE PART
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PartResponseDTO> createPart(
            @RequestBody PartRequestDTO requestDTO) {

        PartResponseDTO responseDTO = partService.createPart(requestDTO);

        return new ResponseEntity<>(
                responseDTO,
                HttpStatus.CREATED);
    }

    // =========================================================
    // GET ALL PARTS
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<PartResponseDTO>> getAllParts() {

        return ResponseEntity.ok(
                partService.getAllParts());
    }

    // =========================================================
    // GET PART BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<PartResponseDTO> getPartById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                partService.getPartById(id));
    }

    // =========================================================
    // UPDATE PART
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PartResponseDTO> updatePart(
            @PathVariable Long id,
            @RequestBody PartRequestDTO requestDTO) {

        return ResponseEntity.ok(
                partService.updatePart(id, requestDTO));
    }

    // =========================================================
    // DELETE PART
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> deletePart(
            @PathVariable Long id) {

        partService.deletePart(id);

        return ResponseEntity.ok(
                "Part deleted successfully");
    }
}