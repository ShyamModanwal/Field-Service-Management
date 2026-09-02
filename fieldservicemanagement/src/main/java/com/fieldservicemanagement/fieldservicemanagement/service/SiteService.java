package com.fieldservicemanagement.fieldservicemanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fieldservicemanagement.fieldservicemanagement.dto.SiteRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.SiteResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.entity.Site;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.exception.CustomerNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.exception.SiteNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.SiteRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================================================
    // CREATE SITE
    // =========================================================

    public SiteResponseDTO createSite(
            Long customerId,
            SiteRequestDTO requestDTO) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));

        Site site = new Site();

        site.setSiteName(requestDTO.getSiteName());
        site.setAddress(requestDTO.getAddress());
        site.setCustomer(customer);

        Site savedSite = siteRepository.save(site);

        return convertToResponseDTO(savedSite);
    }

    // =========================================================
    // GET ALL SITES
    // =========================================================

    public List<SiteResponseDTO> getAllSites() {

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        // ---------------------------------------------------------
        // ADMIN / MANAGER / DISPATCHER
        // ---------------------------------------------------------

        if ("ADMIN".equalsIgnoreCase(role)
                || "MANAGER".equalsIgnoreCase(role)
                || "DISPATCHER".equalsIgnoreCase(role)) {

            return siteRepository.findAll()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .toList();
        }

        // ---------------------------------------------------------
        // CUSTOMER
        // ---------------------------------------------------------

        if ("CUSTOMER".equalsIgnoreCase(role)) {

            if (loggedInUser.getCustomer() == null) {

                throw new RuntimeException(
                        "Customer account is not linked to a customer");
            }

            Long customerId = loggedInUser.getCustomer().getId();

            return siteRepository
                    .findByCustomerId(customerId)
                    .stream()
                    .map(this::convertToResponseDTO)
                    .toList();
        }

        // ---------------------------------------------------------
        // TECHNICIAN
        // ---------------------------------------------------------

        if ("TECHNICIAN".equalsIgnoreCase(role)) {

            /*
             * Technician ko abhi all sites allow kiye ja rahe hain
             * because technician work orders ke through sites par
             * kaam karta hai.
             *
             * Later, agar PDF requirement ke according technician
             * ko sirf assigned work order ke sites dikhane hain,
             * to yahan further restriction laga sakte hain.
             */

            return siteRepository.findAll()
                    .stream()
                    .map(this::convertToResponseDTO)
                    .toList();
        }

        throw new RuntimeException(
                "You are not authorized to view sites");
    }

    // =========================================================
    // GET SITE BY ID
    // =========================================================

    public SiteResponseDTO getSiteById(Long id) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new SiteNotFoundException(
                        "Site not found with id: " + id));

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        // ---------------------------------------------------------
        // ADMIN / MANAGER / DISPATCHER
        // ---------------------------------------------------------

        if ("ADMIN".equalsIgnoreCase(role)
                || "MANAGER".equalsIgnoreCase(role)
                || "DISPATCHER".equalsIgnoreCase(role)) {

            return convertToResponseDTO(site);
        }

        // ---------------------------------------------------------
        // CUSTOMER
        // ---------------------------------------------------------

        if ("CUSTOMER".equalsIgnoreCase(role)) {

            if (loggedInUser.getCustomer() == null) {

                throw new RuntimeException(
                        "Customer account is not linked to a customer");
            }

            Long loggedInCustomerId = loggedInUser.getCustomer().getId();

            if (site.getCustomer() == null
                    || !site.getCustomer()
                            .getId()
                            .equals(loggedInCustomerId)) {

                throw new RuntimeException(
                        "You are not authorized to view this site");
            }

            return convertToResponseDTO(site);
        }

        // ---------------------------------------------------------
        // TECHNICIAN
        // ---------------------------------------------------------

        if ("TECHNICIAN".equalsIgnoreCase(role)) {

            return convertToResponseDTO(site);
        }

        throw new RuntimeException(
                "You are not authorized to view this site");
    }

    // =========================================================
    // UPDATE SITE
    // =========================================================

    public SiteResponseDTO updateSite(
            Long id,
            SiteRequestDTO requestDTO) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new SiteNotFoundException(
                        "Site not found with id: " + id));

        site.setSiteName(requestDTO.getSiteName());
        site.setAddress(requestDTO.getAddress());

        Site updatedSite = siteRepository.save(site);

        return convertToResponseDTO(updatedSite);
    }

    // =========================================================
    // DELETE SITE
    // =========================================================

    public void deleteSite(Long id) {

        if (!siteRepository.existsById(id)) {

            throw new SiteNotFoundException(
                    "Site not found with id: " + id);
        }

        siteRepository.deleteById(id);
    }

    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required");
        }

        String principal = authentication.getName();

        /*
         * JwtAuthenticationFilter:
         *
         * userId|email
         *
         * Example:
         *
         * 3|customerA@test.com
         */

        String[] parts = principal.split("\\|", 2);

        if (parts.length != 2) {

            throw new RuntimeException(
                    "Invalid authentication information");
        }

        Long userId;

        try {

            userId = Long.parseLong(parts[0]);

        } catch (NumberFormatException e) {

            throw new RuntimeException(
                    "Invalid user ID in authentication token");
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Logged-in user not found"));
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private SiteResponseDTO convertToResponseDTO(
            Site site) {

        SiteResponseDTO responseDTO = new SiteResponseDTO();

        responseDTO.setId(
                site.getId());

        responseDTO.setSiteName(
                site.getSiteName());

        responseDTO.setAddress(
                site.getAddress());

        if (site.getCustomer() != null) {

            responseDTO.setCustomerId(
                    site.getCustomer().getId());
        }

        return responseDTO;
    }
}