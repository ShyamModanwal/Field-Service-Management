package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.TimeLogRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.TimeLogResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.TimeLog;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.TimeLogRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public TimeLogService(
            TimeLogRepository timeLogRepository,
            WorkOrderRepository workOrderRepository,
            UserRepository userRepository) {

        this.timeLogRepository = timeLogRepository;
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // CREATE TIME LOG
    // =========================================================

    @Transactional
    public TimeLogResponseDTO createTimeLog(
            Long workOrderId,
            TimeLogRequestDTO requestDTO) {

        // -----------------------------------------------------
        // GET WORK ORDER
        // -----------------------------------------------------

        WorkOrder workOrder = workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "Work Order not found with id: "
                                + workOrderId));

        // -----------------------------------------------------
        // GET LOGGED-IN USER
        // -----------------------------------------------------

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        // -----------------------------------------------------
        // ROLE CHECK
        // -----------------------------------------------------

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"MANAGER".equalsIgnoreCase(role)
                && !"TECHNICIAN".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "You are not authorized to create time logs");
        }

        // -----------------------------------------------------
        // TECHNICIAN ACCESS CHECK
        // -----------------------------------------------------

        if ("TECHNICIAN".equalsIgnoreCase(role)) {

            if (workOrder.getAssignedTo() == null) {

                throw new RuntimeException(
                        "Work Order is not assigned to any technician");
            }

            if (!workOrder.getAssignedTo()
                    .getId()
                    .equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "Technician can only log time on assigned work orders");
            }
        }

        // -----------------------------------------------------
        // REQUEST VALIDATION
        // -----------------------------------------------------

        if (requestDTO == null
                || requestDTO.getStartTime() == null) {

            throw new RuntimeException(
                    "Start time is required");
        }

        if (requestDTO.getEndTime() == null) {

            throw new RuntimeException(
                    "End time is required");
        }

        if (requestDTO.getEndTime()
                .isBefore(requestDTO.getStartTime())) {

            throw new RuntimeException(
                    "End time cannot be before start time");
        }

        // -----------------------------------------------------
        // CALCULATE DURATION
        // -----------------------------------------------------

        long durationMinutes = Duration.between(
                requestDTO.getStartTime(),
                requestDTO.getEndTime())
                .toMinutes();

        if (durationMinutes <= 0) {

            throw new RuntimeException(
                    "Time log duration must be greater than zero");
        }

        // -----------------------------------------------------
        // CREATE TIME LOG
        // -----------------------------------------------------

        TimeLog timeLog = new TimeLog();

        timeLog.setWorkOrder(workOrder);
        timeLog.setTechnician(loggedInUser);
        timeLog.setStartTime(requestDTO.getStartTime());
        timeLog.setEndTime(requestDTO.getEndTime());
        timeLog.setDurationMinutes(durationMinutes);
        timeLog.setNote(requestDTO.getNote());

        TimeLog savedTimeLog = timeLogRepository.save(timeLog);

        return convertToResponseDTO(savedTimeLog);
    }

    // =========================================================
    // GET TIME LOGS BY WORK ORDER
    // =========================================================

    public List<TimeLogResponseDTO> getTimeLogsByWorkOrder(
            Long workOrderId) {

        WorkOrder workOrder = workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "Work Order not found with id: "
                                + workOrderId));

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"MANAGER".equalsIgnoreCase(role)
                && !"TECHNICIAN".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "You are not authorized to view time logs");
        }

        // Technician can only see logs
        // of his assigned work order
        if ("TECHNICIAN".equalsIgnoreCase(role)) {

            if (workOrder.getAssignedTo() == null
                    || !workOrder.getAssignedTo()
                            .getId()
                            .equals(loggedInUser.getId())) {

                throw new RuntimeException(
                        "Technician can only view time logs of assigned work orders");
            }
        }

        return timeLogRepository
                .findByWorkOrderId(workOrderId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET TIME LOGS BY TECHNICIAN
    // =========================================================

    public List<TimeLogResponseDTO> getTimeLogsByTechnician(
            Long technicianId) {

        User loggedInUser = getLoggedInUser();

        String role = loggedInUser.getRole();

        // Technician can only see his own logs
        if ("TECHNICIAN".equalsIgnoreCase(role)
                && !loggedInUser.getId().equals(technicianId)) {

            throw new RuntimeException(
                    "Technician can only view own time logs");
        }

        // Admin / Manager can view any technician's logs
        if (!"ADMIN".equalsIgnoreCase(role)
                && !"MANAGER".equalsIgnoreCase(role)
                && !"TECHNICIAN".equalsIgnoreCase(role)) {

            throw new RuntimeException(
                    "You are not authorized to view time logs");
        }

        // Verify technician exists
        userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException(
                        "Technician not found with id: "
                                + technicianId));

        return timeLogRepository
                .findByTechnicianId(technicianId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required");
        }

        /*
         * JwtAuthenticationFilter mein principal:
         *
         * userId|email
         *
         * Example:
         * 3|technician@gmail.com
         */

        String principal = authentication.getName();

        String[] parts = principal.split("\\|", 2);

        if (parts.length != 2) {

            throw new RuntimeException(
                    "Invalid authentication principal");
        }

        Long userId;

        try {

            userId = Long.parseLong(parts[0]);

        } catch (NumberFormatException e) {

            throw new RuntimeException(
                    "Invalid user ID in authentication token");
        }

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Logged-in user not found"));
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private TimeLogResponseDTO convertToResponseDTO(
            TimeLog timeLog) {

        TimeLogResponseDTO responseDTO = new TimeLogResponseDTO();

        responseDTO.setId(
                timeLog.getId());

        responseDTO.setWorkOrderId(
                timeLog.getWorkOrder()
                        .getId());

        responseDTO.setTechnicianId(
                timeLog.getTechnician()
                        .getId());

        responseDTO.setTechnicianName(
                timeLog.getTechnician()
                        .getName());

        responseDTO.setStartTime(
                timeLog.getStartTime());

        responseDTO.setEndTime(
                timeLog.getEndTime());

        responseDTO.setDurationMinutes(
                timeLog.getDurationMinutes());

        responseDTO.setNote(
                timeLog.getNote());

        return responseDTO;
    }
}