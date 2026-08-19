package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.PartUsageRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.PartUsageResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Part;
import com.fieldservicemanagement.fieldservicemanagement.entity.PartUsage;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.PartRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.PartUsageRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartUsageService {

        private final PartUsageRepository partUsageRepository;
        private final PartRepository partRepository;
        private final WorkOrderRepository workOrderRepository;
        private final UserRepository userRepository;

        public PartUsageService(
                        PartUsageRepository partUsageRepository,
                        PartRepository partRepository,
                        WorkOrderRepository workOrderRepository,
                        UserRepository userRepository) {

                this.partUsageRepository = partUsageRepository;
                this.partRepository = partRepository;
                this.workOrderRepository = workOrderRepository;
                this.userRepository = userRepository;
        }

        // =========================================================
        // USE PART ON WORK ORDER
        // =========================================================

        @Transactional
        public PartUsageResponseDTO usePart(
                        Long workOrderId,
                        PartUsageRequestDTO requestDTO) {

                // -----------------------------------------------------
                // GET WORK ORDER
                // -----------------------------------------------------

                WorkOrder workOrder = workOrderRepository
                                .findById(workOrderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: " + workOrderId));

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
                                        "You are not authorized to use parts");
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
                                                "Technician can only use parts on assigned work orders");
                        }
                }

                // -----------------------------------------------------
                // REQUEST VALIDATION
                // -----------------------------------------------------

                if (requestDTO == null) {

                        throw new RuntimeException(
                                        "Part usage request is required");
                }

                if (requestDTO.getPartId() == null) {

                        throw new RuntimeException(
                                        "Part ID is required");
                }

                if (requestDTO.getQuantity() == null
                                || requestDTO.getQuantity() <= 0) {

                        throw new RuntimeException(
                                        "Quantity must be greater than zero");
                }

                // -----------------------------------------------------
                // GET PART
                // -----------------------------------------------------

                Part part = partRepository
                                .findById(requestDTO.getPartId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Part not found with id: "
                                                                + requestDTO.getPartId()));

                // -----------------------------------------------------
                // STOCK CHECK
                // -----------------------------------------------------

                if (part.getStockQuantity() == null
                                || part.getStockQuantity() < requestDTO.getQuantity()) {

                        int availableStock = part.getStockQuantity() == null
                                        ? 0
                                        : part.getStockQuantity();

                        throw new RuntimeException(
                                        "Insufficient stock. Available stock: "
                                                        + availableStock);
                }

                // -----------------------------------------------------
                // DEDUCT STOCK
                // -----------------------------------------------------

                part.setStockQuantity(
                                part.getStockQuantity()
                                                - requestDTO.getQuantity());

                partRepository.save(part);

                // -----------------------------------------------------
                // CREATE PART USAGE
                // -----------------------------------------------------

                PartUsage partUsage = new PartUsage();

                partUsage.setWorkOrder(workOrder);
                partUsage.setPart(part);
                partUsage.setQuantity(requestDTO.getQuantity());

                // IMPORTANT:
                // used_at database column NOT NULL hai
                partUsage.setUsedAt(LocalDateTime.now());

                PartUsage savedUsage = partUsageRepository.save(partUsage);

                return convertToResponseDTO(savedUsage);
        }

        // =========================================================
        // GET PARTS USED BY WORK ORDER
        // =========================================================

        public List<PartUsageResponseDTO> getPartsUsedByWorkOrder(
                        Long workOrderId) {

                WorkOrder workOrder = workOrderRepository
                                .findById(workOrderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: "
                                                                + workOrderId));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // -----------------------------------------------------
                // ROLE CHECK
                // -----------------------------------------------------

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "You are not authorized to view part usage");
                }

                // -----------------------------------------------------
                // TECHNICIAN ACCESS CHECK
                // -----------------------------------------------------

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician can only view parts of assigned work orders");
                        }
                }

                return partUsageRepository
                                .findByWorkOrderId(workOrderId)
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

                String principal = authentication.getName();

                // Principal format:
                // userId|email

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

        private PartUsageResponseDTO convertToResponseDTO(
                        PartUsage partUsage) {

                PartUsageResponseDTO responseDTO = new PartUsageResponseDTO();

                responseDTO.setId(
                                partUsage.getId());

                if (partUsage.getWorkOrder() != null) {
                        responseDTO.setWorkOrderId(
                                        partUsage.getWorkOrder().getId());
                }

                if (partUsage.getPart() != null) {
                        responseDTO.setPartId(
                                        partUsage.getPart().getId());

                        responseDTO.setPartNumber(
                                        partUsage.getPart().getPartNumber());

                        responseDTO.setPartName(
                                        partUsage.getPart().getName());
                }

                responseDTO.setQuantity(
                                partUsage.getQuantity());

                return responseDTO;
        }
}