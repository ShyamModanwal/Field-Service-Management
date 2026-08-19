package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.UserRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.UserResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        User user = new User();

        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setRole(requestDTO.getRole());

        // Plain password ko BCrypt hash me convert karna
        user.setPasswordHash(
                passwordEncoder.encode(requestDTO.getPassword()));

        User savedUser = userRepository.save(user);

        // Entity -> Response DTO
        UserResponseDTO responseDTO = new UserResponseDTO();

        responseDTO.setId(savedUser.getId());
        responseDTO.setName(savedUser.getName());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setRole(savedUser.getRole());

        return responseDTO;
    }

    // GET ALL USERS
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> {

                    UserResponseDTO responseDTO = new UserResponseDTO();

                    responseDTO.setId(user.getId());
                    responseDTO.setName(user.getName());
                    responseDTO.setEmail(user.getEmail());
                    responseDTO.setRole(user.getRole());

                    return responseDTO;
                })
                .toList();
    }
}