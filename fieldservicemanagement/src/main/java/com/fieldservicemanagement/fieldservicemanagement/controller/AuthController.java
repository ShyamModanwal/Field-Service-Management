package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.LoginRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.LoginResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO requestDTO) {

        LoginResponseDTO responseDTO = authService.login(requestDTO);

        return ResponseEntity.ok(responseDTO);
    }
}