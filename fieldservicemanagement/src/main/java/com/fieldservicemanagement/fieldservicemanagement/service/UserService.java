package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.UserRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.UserResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    public UserResponseDTO createUser(
            UserRequestDTO requestDTO) {

        String role = requestDTO.getRole()
                .trim()
                .toUpperCase();

        // ---------------------------------------------------------
        // CUSTOMER USER
        // ---------------------------------------------------------

        Customer customer = null;

        if ("CUSTOMER".equals(role)) {

            if (requestDTO.getCustomerId() == null) {

                throw new RuntimeException(
                        "Customer ID is required for CUSTOMER user");
            }

            customer = customerRepository
                    .findById(requestDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException(
                            "Customer not found with id: "
                                    + requestDTO.getCustomerId()));
        }

        // ---------------------------------------------------------
        // CREATE USER
        // ---------------------------------------------------------

        User user = new User();

        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setRole(role);

        // Plain password -> BCrypt hash
        user.setPasswordHash(
                passwordEncoder.encode(
                        requestDTO.getPassword()));

        // CUSTOMER relationship
        user.setCustomer(customer);

        User savedUser = userRepository.save(user);

        return convertToResponseDTO(savedUser);
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private UserResponseDTO convertToResponseDTO(
            User user) {

        UserResponseDTO responseDTO =
                new UserResponseDTO();

        responseDTO.setId(
                user.getId());

        responseDTO.setName(
                user.getName());

        responseDTO.setEmail(
                user.getEmail());

        responseDTO.setRole(
                user.getRole());

        if (user.getCustomer() != null) {

            responseDTO.setCustomerId(
                    user.getCustomer().getId());
        }

        return responseDTO;
    }
}