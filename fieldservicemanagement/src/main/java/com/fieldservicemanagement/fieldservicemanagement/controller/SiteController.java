package com.fieldservicemanagement.fieldservicemanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SiteResponseDTO> createSite(
            @PathVariable Long customerId,
            @RequestBody SiteRequestDTO requestDTO) {

        SiteResponseDTO responseDTO = siteService.createSite(customerId, requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // GET ALL SITES
    @GetMapping("/sites")
    public ResponseEntity<List<SiteResponseDTO>> getAllSites() {

        List<SiteResponseDTO> sites = siteService.getAllSites();

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    // GET SITE BY ID
    @GetMapping("/sites/{id}")
    public ResponseEntity<SiteResponseDTO> getSiteById(
            @PathVariable Long id) {

        SiteResponseDTO responseDTO = siteService.getSiteById(id);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/sites/{id}")
    public ResponseEntity<SiteResponseDTO> updateSite(
            @PathVariable Long id,
            @RequestBody SiteRequestDTO requestDTO) {

        SiteResponseDTO responseDTO = siteService.updateSite(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    // DELETE SITE
    @DeleteMapping("/sites/{id}")
    public ResponseEntity<String> deleteSite(
            @PathVariable Long id) {

        siteService.deleteSite(id);

        return ResponseEntity.ok("Site deleted successfully");
    }
}