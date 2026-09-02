package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CustomerResponseDTO saveCustomer(
            @RequestBody CustomerRequestDTO requestDTO) {

        return customerService.saveCustomer(requestDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<CustomerResponseDTO> getAllCustomers() {

        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CustomerResponseDTO getCustomerById(
            @PathVariable Long id) {

        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public CustomerResponseDTO updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequestDTO requestDTO) {

        return customerService.updateCustomer(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return "Customer deleted successfully";
    }
}