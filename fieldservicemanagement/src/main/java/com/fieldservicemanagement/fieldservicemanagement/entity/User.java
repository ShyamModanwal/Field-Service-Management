package com.fieldservicemanagement.fieldservicemanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String role;

    private String passwordHash;

    // Customer ke saath relationship
    // Sirf CUSTOMER role wale user ke liye customer set hoga
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(
            String name,
            String email,
            String role,
            String passwordHash) {

        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    // =========================================================
    // GETTER & SETTER - ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // =========================================================
    // GETTER & SETTER - NAME
    // =========================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================================
    // GETTER & SETTER - EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================================
    // GETTER & SETTER - ROLE
    // =========================================================

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // =========================================================
    // GETTER & SETTER - PASSWORD
    // =========================================================

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // =========================================================
    // GETTER & SETTER - CUSTOMER
    // =========================================================

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}