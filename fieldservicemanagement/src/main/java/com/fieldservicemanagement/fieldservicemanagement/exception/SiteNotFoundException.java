package com.fieldservicemanagement.fieldservicemanagement.exception;

public class SiteNotFoundException extends RuntimeException {

    public SiteNotFoundException(String message) {
        super(message);
    }
}