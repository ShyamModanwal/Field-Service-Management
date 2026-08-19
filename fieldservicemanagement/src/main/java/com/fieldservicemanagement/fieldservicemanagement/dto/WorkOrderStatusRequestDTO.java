package com.fieldservicemanagement.fieldservicemanagement.dto;

public class WorkOrderStatusRequestDTO {

    private String status;

    private String note;

    public WorkOrderStatusRequestDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}