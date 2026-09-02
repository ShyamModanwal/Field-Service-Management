package com.fieldservicemanagement.fieldservicemanagement.dto;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Long customerId;

    public UserResponseDTO() {
    }

    // =========================================================
    // ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =========================================================
    // NAME
    // =========================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================================
    // EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================================
    // ROLE
    // =========================================================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =========================================================
    // CUSTOMER ID
    // =========================================================

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}