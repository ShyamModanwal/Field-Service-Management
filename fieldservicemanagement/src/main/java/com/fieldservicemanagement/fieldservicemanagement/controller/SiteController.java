package com.fieldservicemanagement.fieldservicemanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fieldservicemanagement.fieldservicemanagement.dto.SiteRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.SiteResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.SiteService;

@RestController
@RequestMapping("/api/customers")
public class SiteController {

    @Autowired
    private SiteService siteService;

    // CREATE SITE
    @PostMapping("/{customerId}/sites")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SiteResponseDTO> createSite(
            @PathVariable Long customerId,
            @RequestBody SiteRequestDTO requestDTO) {

        return new ResponseEntity<>(
                siteService.createSite(customerId, requestDTO),
                HttpStatus.CREATED);
    }

    // GET ALL SITES
    @GetMapping("/sites")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<SiteResponseDTO>> getAllSites() {

        return ResponseEntity.ok(
                siteService.getAllSites());
    }

    // GET SITE BY ID
    @GetMapping("/sites/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<SiteResponseDTO> getSiteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                siteService.getSiteById(id));
    }

    // UPDATE SITE
    @PutMapping("/sites/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SiteResponseDTO> updateSite(
            @PathVariable Long id,
            @RequestBody SiteRequestDTO requestDTO) {

        return ResponseEntity.ok(
                siteService.updateSite(id, requestDTO));
    }

    // DELETE SITE
    @DeleteMapping("/sites/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> deleteSite(
            @PathVariable Long id) {

        siteService.deleteSite(id);

        return ResponseEntity.ok(
                "Site deleted successfully");
    }
}