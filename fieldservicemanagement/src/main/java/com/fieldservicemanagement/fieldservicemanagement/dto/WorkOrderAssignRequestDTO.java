package com.fieldservicemanagement.fieldservicemanagement.dto;

public class WorkOrderAssignRequestDTO {

    private Long technicianId;

    public WorkOrderAssignRequestDTO() {
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}