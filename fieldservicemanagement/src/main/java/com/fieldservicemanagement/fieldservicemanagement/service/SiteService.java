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
    public SiteResponseDTO createSite(Long customerId, SiteRequestDTO requestDTO) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));

        Site site = new Site();

        site.setSiteName(requestDTO.getSiteName());
        site.setAddress(requestDTO.getAddress());

        site.setCustomer(customer);

        Site savedSite = siteRepository.save(site);

        SiteResponseDTO responseDTO = new SiteResponseDTO();

        responseDTO.setId(savedSite.getId());
        responseDTO.setSiteName(savedSite.getSiteName());
        responseDTO.setAddress(savedSite.getAddress());

        return responseDTO;
    }

    // GET ALL SITES
    public List<SiteResponseDTO> getAllSites() {

        List<Site> sites = siteRepository.findAll();

        List<SiteResponseDTO> responseList = new ArrayList<>();

        for (Site site : sites) {

            SiteResponseDTO responseDTO = new SiteResponseDTO();

            responseDTO.setId(site.getId());
            responseDTO.setSiteName(site.getSiteName());
            responseDTO.setAddress(site.getAddress());

            responseList.add(responseDTO);
        }

        return responseList;
    }

    // GET SITE BY ID
    public SiteResponseDTO getSiteById(Long id) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new SiteNotFoundException(
                        "Site not found with id: " + id));

        SiteResponseDTO responseDTO = new SiteResponseDTO();

        responseDTO.setId(site.getId());
        responseDTO.setSiteName(site.getSiteName());
        responseDTO.setAddress(site.getAddress());

        return responseDTO;
    }

    public SiteResponseDTO updateSite(Long id, SiteRequestDTO requestDTO) {

        // 1. Find existing site
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new SiteNotFoundException(
                        "Site not found with id: " + id));

        // 2. Update data
        site.setSiteName(requestDTO.getSiteName());
        site.setAddress(requestDTO.getAddress());

        // 3. Save updated site
        Site updatedSite = siteRepository.save(site);

        // 4. Convert Entity to ResponseDTO
        SiteResponseDTO responseDTO = new SiteResponseDTO();

        responseDTO.setId(updatedSite.getId());
        responseDTO.setSiteName(updatedSite.getSiteName());
        responseDTO.setAddress(updatedSite.getAddress());

        // 5. Return response
        return responseDTO;
    }
    // DELETE SITE
public void deleteSite(Long id) {

    if (!siteRepository.existsById(id)) {
        throw new SiteNotFoundException(
                "Site not found with id: " + id);
    }

    siteRepository.deleteById(id);
}
}