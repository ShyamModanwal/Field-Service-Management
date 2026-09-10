package com.fieldservicemanagement.fieldservicemanagement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash = encoder.encode("Admin@123");

        System.out.println(hash);
    }
}