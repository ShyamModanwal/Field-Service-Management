package com.fieldservicemanagement.fieldservicemanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.exception.CustomerNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    public CustomerResponseDTO saveCustomer(
            CustomerRequestDTO requestDTO) {

        Customer customer = new Customer();

        customer.setCustomerName(
                requestDTO.getCustomerName());

        customer.setEmail(
                requestDTO.getEmail());

        customer.setPhone(
                requestDTO.getPhone());

        customer.setCompanyName(
                requestDTO.getCompanyName());

        customer.setAddress(
                requestDTO.getAddress());

        Customer savedCustomer =
                customerRepository.save(customer);

        return convertToResponseDTO(savedCustomer);
    }

    // =========================================================
    // GET ALL CUSTOMERS
    // ADMIN / MANAGER
    // =========================================================

    public List<CustomerResponseDTO> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    public CustomerResponseDTO getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(
                                "Customer not found with id: " + id));

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        // =====================================================
        // CUSTOMER -> ONLY OWN CUSTOMER
        // =====================================================

        if ("CUSTOMER".equalsIgnoreCase(role)) {

            if (loggedInUser.getCustomer() == null) {

                throw new RuntimeException(
                        "Customer account is not linked to any customer");
            }

            Long loggedInCustomerId =
                    loggedInUser.getCustomer().getId();

            if (!loggedInCustomerId.equals(id)) {

                throw new RuntimeException(
                        "You are not authorized to view this customer");
            }
        }

        // ADMIN / MANAGER can view anyone
        if (!"ADMIN".equalsIgnoreCase(role)
                && !"MANAGER".equalsIgnoreCase(role)
                && !"CUSTOMER".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "You are not authorized to view customer");
        }

        return convertToResponseDTO(customer);
    }

    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    public CustomerResponseDTO updateCustomer(
            Long id,
            CustomerRequestDTO requestDTO) {

        Customer existingCustomer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: " + id));

        existingCustomer.setCustomerName(
                requestDTO.getCustomerName());

        existingCustomer.setEmail(
                requestDTO.getEmail());

        existingCustomer.setPhone(
                requestDTO.getPhone());

        existingCustomer.setCompanyName(
                requestDTO.getCompanyName());

        existingCustomer.setAddress(
                requestDTO.getAddress());

        Customer updatedCustomer =
                customerRepository.save(existingCustomer);

        return convertToResponseDTO(updatedCustomer);
    }

    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    public void deleteCustomer(Long id) {

        if (!customerRepository.existsById(id)) {

            throw new CustomerNotFoundException(
                    "Customer not found with id: " + id);
        }

        customerRepository.deleteById(id);
    }

    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required");
        }

        /*
         * JWT principal format:
         *
         * userId|email
         *
         * Example:
         * 17|customer@test.com
         */

        String principal =
                authentication.getName();

        String[] parts =
                principal.split("\\|", 2);

        if (parts.length != 2) {

            throw new RuntimeException(
                    "Invalid authentication information");
        }

        Long userId;

        try {

            userId =
                    Long.parseLong(parts[0]);

        } catch (NumberFormatException e) {

            throw new RuntimeException(
                    "Invalid user ID in authentication token");
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"));
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private CustomerResponseDTO convertToResponseDTO(
            Customer customer) {

        CustomerResponseDTO responseDTO =
                new CustomerResponseDTO();

        responseDTO.setId(
                customer.getId());

        responseDTO.setCustomerName(
                customer.getCustomerName());

        responseDTO.setEmail(
                customer.getEmail());

        responseDTO.setPhone(
                customer.getPhone());

        responseDTO.setCompanyName(
                customer.getCompanyName());

        responseDTO.setAddress(
                customer.getAddress());

        return responseDTO;
    }
}