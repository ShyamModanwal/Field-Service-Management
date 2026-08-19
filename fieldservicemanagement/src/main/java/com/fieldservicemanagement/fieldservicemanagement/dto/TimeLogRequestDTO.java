package com.fieldservicemanagement.fieldservicemanagement.dto;

import java.time.LocalDateTime;

public class TimeLogRequestDTO {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String note;

    public TimeLogRequestDTO() {
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}