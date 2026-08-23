package com.fieldservicemanagement.fieldservicemanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fieldservicemanagement.fieldservicemanagement.dto.SiteRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.SiteResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.entity.Site;
import com.fieldservicemanagement.fieldservicemanagement.exception.CustomerNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.exception.SiteNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.SiteRepository;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // CREATE SITE
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

    // GET ALL SITES
    public List<SiteResponseDTO> getAllSites() {

        List<Site> sites = siteRepository.findAll();

        List<SiteResponseDTO> responseList = new ArrayList<>();

        for (Site site : sites) {

            responseList.add(
                    convertToResponseDTO(site));
        }

        return responseList;
    }

    // GET SITE BY ID
    public SiteResponseDTO getSiteById(Long id) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new SiteNotFoundException(
                        "Site not found with id: " + id));

        return convertToResponseDTO(site);
    }

    // UPDATE SITE
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

    // DELETE SITE
    public void deleteSite(Long id) {

        if (!siteRepository.existsById(id)) {

            throw new SiteNotFoundException(
                    "Site not found with id: " + id);
        }

        siteRepository.deleteById(id);
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private SiteResponseDTO convertToResponseDTO(
            Site site) {

        SiteResponseDTO responseDTO = new SiteResponseDTO();

        responseDTO.setId(site.getId());
        responseDTO.setSiteName(site.getSiteName());
        responseDTO.setAddress(site.getAddress());

        if (site.getCustomer() != null) {

            responseDTO.setCustomerId(
                    site.getCustomer().getId());
        }

        return responseDTO;
    }
}