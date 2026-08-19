package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusHistoryResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.entity.Site;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrderStatusHistory;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.SiteRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderStatusHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderAssignRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkOrderService {

        @Autowired
        private WorkOrderRepository workOrderRepository;

        @Autowired
        private CustomerRepository customerRepository;

        @Autowired
        private SiteRepository siteRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private WorkOrderStatusHistoryRepository statusHistoryRepository;

        // =========================================================
        // CREATE WORK ORDER
        // =========================================================

        public WorkOrderResponseDTO saveWorkOrder(
                        WorkOrderRequestDTO requestDTO) {

                WorkOrder workOrder = new WorkOrder();

                workOrder.setCode(requestDTO.getCode());
                workOrder.setTitle(requestDTO.getTitle());
                workOrder.setDescription(requestDTO.getDescription());
                workOrder.setPriority(requestDTO.getPriority());
                workOrder.setStatus(requestDTO.getStatus());
                workOrder.setSlaDueAt(requestDTO.getSlaDueAt());

                Customer customer = customerRepository
                                .findById(requestDTO.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                Site site = siteRepository
                                .findById(requestDTO.getSiteId())
                                .orElseThrow(() -> new RuntimeException("Site not found"));

                workOrder.setCustomer(customer);
                workOrder.setSite(site);

                // assignedTo optional hai
                if (requestDTO.getAssignedToId() != null) {

                        User user = userRepository
                                        .findById(requestDTO.getAssignedToId())
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        // Sirf TECHNICIAN ko assign kar sakte hain
                        if (!"TECHNICIAN".equalsIgnoreCase(user.getRole())) {

                                throw new RuntimeException(
                                                "Work Order can only be assigned to a TECHNICIAN");
                        }

                        workOrder.setAssignedTo(user);

                        // Agar technician assign hai
                        // to initial status ASSIGNED hoga
                        workOrder.setStatus("ASSIGNED");
                }

                // Agar status nahi diya gaya
                // to default NEW
                if (workOrder.getStatus() == null ||
                                workOrder.getStatus().isBlank()) {

                        workOrder.setStatus("NEW");
                }

                WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

                return convertToResponseDTO(savedWorkOrder);
        }

        // =========================================================
        // GET ALL WORK ORDERS
        // =========================================================

        public List<WorkOrderResponseDTO> getAllWorkOrders() {

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // ADMIN aur MANAGER sabhi work orders dekh sakte hain
                if ("ADMIN".equalsIgnoreCase(role)
                                || "MANAGER".equalsIgnoreCase(role)) {

                        return workOrderRepository.findAll()
                                        .stream()
                                        .map(this::convertToResponseDTO)
                                        .toList();
                }

                // TECHNICIAN sirf apne assigned work orders dekhega
                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        return workOrderRepository
                                        .findByAssignedToId(loggedInUser.getId())
                                        .stream()
                                        .map(this::convertToResponseDTO)
                                        .toList();
                }

                throw new RuntimeException(
                                "You are not authorized to view work orders");
        }

        // =========================================================
        // GET WORK ORDERS ASSIGNED TO A TECHNICIAN
        // =========================================================

        public List<WorkOrderResponseDTO> getWorkOrdersByTechnician(
                        Long userId) {

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // Technician sirf apne ID se request kar sakta hai
                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (!loggedInUser.getId().equals(userId)) {

                                throw new RuntimeException(
                                                "Technician can only view assigned work orders");
                        }
                }

                // ADMIN / MANAGER kisi bhi technician ke jobs dekh sakte hain
                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "You are not authorized to view technician work orders");
                }

                return workOrderRepository
                                .findByAssignedToId(userId)
                                .stream()
                                .map(this::convertToResponseDTO)
                                .toList();
        }

        // =========================================================
        // GET WORK ORDER BY ID
        // =========================================================

        public WorkOrderResponseDTO getWorkOrderById(Long id) {

                WorkOrder workOrder = workOrderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: " + id));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // Technician sirf apna assigned Work Order dekh sakta hai
                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician can only view assigned work orders");
                        }
                }

                // Sirf authorized roles ko access
                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "You are not authorized to view this work order");
                }

                return convertToResponseDTO(workOrder);
        }

        // =========================================================
        // UPDATE WORK ORDER
        // =========================================================

        public WorkOrderResponseDTO updateWorkOrder(
                        Long id,
                        WorkOrderRequestDTO requestDTO) {

                WorkOrder existingWorkOrder = workOrderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: "
                                                                + id));
                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (existingWorkOrder.getAssignedTo() == null
                                        || !existingWorkOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician can only update assigned work orders");
                        }
                }

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "You are not authorized to update work orders");
                }

                // CLOSED aur CANCELLED work order immutable hai
                if ("CLOSED".equals(existingWorkOrder.getStatus())
                                || "CANCELLED".equals(existingWorkOrder.getStatus())) {

                        throw new RuntimeException(
                                        "Closed or cancelled work order cannot be edited");
                }

                existingWorkOrder.setCode(requestDTO.getCode());
                existingWorkOrder.setTitle(requestDTO.getTitle());
                existingWorkOrder.setDescription(
                                requestDTO.getDescription());
                existingWorkOrder.setPriority(requestDTO.getPriority());
                existingWorkOrder.setSlaDueAt(
                                requestDTO.getSlaDueAt());

                Customer customer = customerRepository.findById(
                                requestDTO.getCustomerId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Customer not found"));

                Site site = siteRepository.findById(
                                requestDTO.getSiteId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Site not found"));

                existingWorkOrder.setCustomer(customer);
                existingWorkOrder.setSite(site);

                // Assignment update
                if (requestDTO.getAssignedToId() != null) {

                        User user = userRepository.findById(
                                        requestDTO.getAssignedToId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "User not found"));

                        if (!"TECHNICIAN".equalsIgnoreCase(
                                        user.getRole())) {

                                throw new RuntimeException(
                                                "Work Order can only be assigned to a TECHNICIAN");
                        }

                        existingWorkOrder.setAssignedTo(user);

                } else {

                        existingWorkOrder.setAssignedTo(null);
                }

                /*
                 * IMPORTANT:
                 *
                 * Status ko yahan directly update nahi karenge.
                 *
                 * Status change ke liye dedicated
                 * changeStatus() method use hoga.
                 *
                 * Isse lifecycle bypass nahi ho sakta.
                 */

                WorkOrder updatedWorkOrder = workOrderRepository.save(existingWorkOrder);

                return convertToResponseDTO(updatedWorkOrder);
        }
        // =========================================================
        // ASSIGN WORK ORDER TO TECHNICIAN
        // =========================================================

        @Transactional
        public WorkOrderResponseDTO assignWorkOrder(
                        Long workOrderId,
                        WorkOrderAssignRequestDTO requestDTO) {

                WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: " + workOrderId));

                // ---------------------------------------------------------
                // GET LOGGED-IN USER
                // ---------------------------------------------------------

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // ---------------------------------------------------------
                // ONLY ADMIN / MANAGER / DISPATCHER CAN ASSIGN
                // ---------------------------------------------------------

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"DISPATCHER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Only ADMIN, MANAGER or DISPATCHER can assign work orders");
                }

                // ---------------------------------------------------------
                // TECHNICIAN ID CHECK
                // ---------------------------------------------------------

                if (requestDTO == null
                                || requestDTO.getTechnicianId() == null) {

                        throw new RuntimeException(
                                        "Technician ID is required");
                }

                // ---------------------------------------------------------
                // FIND TECHNICIAN
                // ---------------------------------------------------------

                User technician = userRepository
                                .findById(requestDTO.getTechnicianId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Technician not found"));

                // ---------------------------------------------------------
                // ROLE VALIDATION
                // ---------------------------------------------------------

                if (!"TECHNICIAN".equalsIgnoreCase(
                                technician.getRole())) {

                        throw new RuntimeException(
                                        "Work Order can only be assigned to a TECHNICIAN");
                }

                // ---------------------------------------------------------
                // CLOSED / CANCELLED WORK ORDER
                // ---------------------------------------------------------

                if ("CLOSED".equalsIgnoreCase(workOrder.getStatus())
                                || "CANCELLED".equalsIgnoreCase(workOrder.getStatus())) {

                        throw new RuntimeException(
                                        "Closed or cancelled work order cannot be assigned");
                }

                // ---------------------------------------------------------
                // ASSIGN TECHNICIAN
                // ---------------------------------------------------------

                workOrder.setAssignedTo(technician);

                // Assignment means status becomes ASSIGNED
                workOrder.setStatus("ASSIGNED");

                WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);

                return convertToResponseDTO(updatedWorkOrder);
        }

        // =========================================================
        // CHANGE WORK ORDER STATUS
        // =========================================================

        @Transactional
        public WorkOrderResponseDTO changeStatus(
                        Long id,
                        WorkOrderStatusRequestDTO requestDTO) {

                WorkOrder workOrder = workOrderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: "
                                                                + id));

                String currentStatus = workOrder.getStatus();

                String newStatus = requestDTO.getStatus();

                // Null / empty status check
                if (newStatus == null ||
                                newStatus.isBlank()) {

                        throw new RuntimeException(
                                        "New status is required");
                }

                // Status uppercase mein convert karna
                newStatus = newStatus.trim().toUpperCase();

                // Same status dobara set nahi kar sakte
                if (currentStatus != null &&
                                currentStatus.equalsIgnoreCase(newStatus)) {

                        throw new RuntimeException(
                                        "Work Order is already in status: "
                                                        + currentStatus);
                }

                // Current status check
                if (currentStatus == null ||
                                currentStatus.isBlank()) {

                        throw new RuntimeException(
                                        "Current work order status is missing");
                }

                currentStatus = currentStatus.trim().toUpperCase();

                // =====================================================
                // GET LOGGED-IN USER FROM JWT
                // =====================================================

                User loggedInUser = getLoggedInUser();

                // =====================================================
                // CHECK ROLE PERMISSION
                // =====================================================

                validateStatusPermission(
                                workOrder,
                                loggedInUser,
                                currentStatus,
                                newStatus);

                // =====================================================
                // CHECK LIFECYCLE TRANSITION
                // =====================================================

                if (!isValidTransition(
                                currentStatus,
                                newStatus)) {

                        throw new RuntimeException(
                                        "Invalid status transition from "
                                                        + currentStatus
                                                        + " to "
                                                        + newStatus);
                }

                // =====================================================
                // UPDATE WORK ORDER STATUS
                // =====================================================

                workOrder.setStatus(newStatus);

                WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);

                // =====================================================
                // CREATE STATUS HISTORY
                // =====================================================

                WorkOrderStatusHistory history = new WorkOrderStatusHistory();

                history.setWorkOrder(updatedWorkOrder);

                history.setFromStatus(currentStatus);

                history.setToStatus(newStatus);

                history.setChangedBy(loggedInUser);

                history.setChangedAt(LocalDateTime.now());

                history.setNote(requestDTO.getNote());

                statusHistoryRepository.save(history);

                return convertToResponseDTO(
                                updatedWorkOrder);
        }

        // =========================================================
        // VALIDATE STATUS PERMISSION
        // =========================================================

        private void validateStatusPermission(
                        WorkOrder workOrder,
                        User loggedInUser,
                        String currentStatus,
                        String newStatus) {

                String role = loggedInUser.getRole();

                // =====================================================
                // TECHNICIAN
                // =====================================================

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        // Technician sirf apne assigned work order
                        // par action kar sakta hai

                        if (workOrder.getAssignedTo() == null) {

                                throw new RuntimeException(
                                                "Work Order is not assigned to any technician");
                        }

                        if (!workOrder.getAssignedTo()
                                        .getId()
                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician can only act on assigned work orders");
                        }

                        // Technician CLOSED nahi kar sakta
                        if ("CLOSED".equals(newStatus)) {

                                throw new RuntimeException(
                                                "Technician cannot close a work order");
                        }

                        // Technician CANCELLED bhi nahi karega
                        if ("CANCELLED".equals(newStatus)) {

                                throw new RuntimeException(
                                                "Technician cannot cancel a work order");
                        }

                        return;
                }

                // =====================================================
                // MANAGER / ADMIN
                // =====================================================

                if ("MANAGER".equalsIgnoreCase(role)
                                || "ADMIN".equalsIgnoreCase(role)) {

                        // Manager/Admin ko allowed lifecycle transitions
                        // follow karne honge

                        return;
                }

                // =====================================================
                // DISPATCHER
                // =====================================================

                if ("DISPATCHER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Dispatcher cannot change work order status");
                }

                // =====================================================
                // CUSTOMER
                // =====================================================

                if ("CUSTOMER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Customer cannot change work order status");
                }

                // Unknown role
                throw new RuntimeException(
                                "User role is not authorized to change work order status");
        }

        // =========================================================
        // CHECK VALID STATUS TRANSITION
        // =========================================================

        private boolean isValidTransition(
                        String currentStatus,
                        String newStatus) {

                return switch (currentStatus) {

                        // NEW
                        case "NEW" ->
                                newStatus.equals("ASSIGNED")
                                                || newStatus.equals("CANCELLED");

                        // ASSIGNED
                        case "ASSIGNED" ->
                                newStatus.equals("IN_PROGRESS")
                                                || newStatus.equals("CANCELLED");

                        // IN_PROGRESS
                        case "IN_PROGRESS" ->
                                newStatus.equals("ON_HOLD")
                                                || newStatus.equals("COMPLETED");

                        // ON_HOLD
                        case "ON_HOLD" ->
                                newStatus.equals("IN_PROGRESS")
                                                || newStatus.equals("CANCELLED");

                        // COMPLETED
                        case "COMPLETED" ->
                                newStatus.equals("CLOSED");

                        // Terminal states
                        case "CLOSED", "CANCELLED" ->
                                false;

                        default ->
                                false;
                };
        }

        // =========================================================
        // GET LOGGED-IN USER
        // =========================================================

        private User getLoggedInUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !authentication.isAuthenticated()) {

                        throw new RuntimeException(
                                        "Authentication required");
                }

                /*
                 * JwtAuthenticationFilter mein principal ka format:
                 *
                 * userId|email
                 *
                 * Example:
                 * 2|technician@gmail.com
                 */

                String principal = authentication.getName();

                String[] parts = principal.split("\\|", 2);

                if (parts.length != 2) {

                        throw new RuntimeException(
                                        "Invalid authentication information");
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
        // DELETE WORK ORDER
        // =========================================================

        public void deleteWorkOrder(Long id) {

                if (!workOrderRepository.existsById(id)) {

                        throw new RuntimeException(
                                        "Work Order not found with id: "
                                                        + id);
                }

                workOrderRepository.deleteById(id);
        }

        // =========================================================
        // ENTITY → RESPONSE DTO
        // =========================================================

        private WorkOrderResponseDTO convertToResponseDTO(
                        WorkOrder workOrder) {

                WorkOrderResponseDTO responseDTO = new WorkOrderResponseDTO();

                responseDTO.setId(
                                workOrder.getId());

                responseDTO.setCode(
                                workOrder.getCode());

                responseDTO.setTitle(
                                workOrder.getTitle());

                responseDTO.setDescription(
                                workOrder.getDescription());

                responseDTO.setPriority(
                                workOrder.getPriority());

                responseDTO.setStatus(
                                workOrder.getStatus());

                responseDTO.setSlaDueAt(
                                workOrder.getSlaDueAt());

                if (workOrder.getCustomer() != null) {

                        responseDTO.setCustomerId(
                                        workOrder.getCustomer().getId());
                }

                if (workOrder.getSite() != null) {

                        responseDTO.setSiteId(
                                        workOrder.getSite().getId());
                }

                if (workOrder.getAssignedTo() != null) {

                        responseDTO.setAssignedToId(
                                        workOrder.getAssignedTo().getId());
                }

                return responseDTO;
        }
        // =========================================================
        // GET WORK ORDER STATUS HISTORY
        // =========================================================

        public List<WorkOrderStatusHistoryResponseDTO> getWorkOrderStatusHistory(
                        Long workOrderId) {

                WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found with id: " + workOrderId));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole();

                // =====================================================
                // TECHNICIAN
                // =====================================================

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician can only view history of assigned work orders");
                        }
                }

                // =====================================================
                // ALLOWED ROLES
                // =====================================================

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "You are not authorized to view work order history");
                }

                return statusHistoryRepository
                                .findByWorkOrderIdOrderByChangedAtAsc(workOrderId)
                                .stream()
                                .map(this::convertToStatusHistoryResponseDTO)
                                .toList();
        }

        // =========================================================
        // STATUS HISTORY ENTITY → RESPONSE DTO
        // =========================================================

        private WorkOrderStatusHistoryResponseDTO convertToStatusHistoryResponseDTO(
                        WorkOrderStatusHistory history) {

                WorkOrderStatusHistoryResponseDTO responseDTO = new WorkOrderStatusHistoryResponseDTO();

                responseDTO.setId(history.getId());

                if (history.getWorkOrder() != null) {

                        responseDTO.setWorkOrderId(
                                        history.getWorkOrder().getId());
                }

                responseDTO.setFromStatus(
                                history.getFromStatus());

                responseDTO.setToStatus(
                                history.getToStatus());

                if (history.getChangedBy() != null) {

                        responseDTO.setChangedById(
                                        history.getChangedBy().getId());

                        responseDTO.setChangedByEmail(
                                        history.getChangedBy().getEmail());
                }

                responseDTO.setChangedAt(
                                history.getChangedAt());

                responseDTO.setNote(
                                history.getNote());

                return responseDTO;
        }
}