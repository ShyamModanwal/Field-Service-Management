package com.fieldservicemanagement.fieldservicemanagement.dto;

public class SiteRequestDTO {

    private String siteName;
    private String address;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}