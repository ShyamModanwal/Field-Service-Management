package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderAssignRequestDTO;
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
        private WorkOrderStatusHistoryRepository workOrderStatusHistoryRepository;

        // =========================================================
        // CREATE WORK ORDER
        // =========================================================

        public WorkOrderResponseDTO saveWorkOrder(
                        WorkOrderRequestDTO requestDTO) {

                Customer customer = customerRepository
                                .findById(requestDTO.getCustomerId())
                                .orElseThrow(() -> new RuntimeException("Customer not found"));

                Site site = siteRepository
                                .findById(requestDTO.getSiteId())
                                .orElseThrow(() -> new RuntimeException("Site not found"));

                WorkOrder workOrder = new WorkOrder();

                workOrder.setCode(requestDTO.getCode());
                workOrder.setTitle(requestDTO.getTitle());
                workOrder.setDescription(requestDTO.getDescription());
                workOrder.setPriority(requestDTO.getPriority());
                workOrder.setStatus(requestDTO.getStatus());
                workOrder.setSlaDueAt(requestDTO.getSlaDueAt());

                workOrder.setCustomer(customer);
                workOrder.setSite(site);

                if (requestDTO.getAssignedToId() != null) {

                        User technician = userRepository
                                        .findById(requestDTO.getAssignedToId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Assigned user not found"));

                        workOrder.setAssignedTo(technician);
                }

                WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

                return convertToResponseDTO(savedWorkOrder);
        }

        // =========================================================
        // GET ALL WORK ORDERS
        // =========================================================

        public List<WorkOrderResponseDTO> getAllWorkOrders() {

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // ADMIN, MANAGER and DISPATCHER
                // can see all work orders

                if ("ADMIN".equalsIgnoreCase(role)
                                || "MANAGER".equalsIgnoreCase(role)
                                || "DISPATCHER".equalsIgnoreCase(role)) {

                        return workOrderRepository.findAll()
                                        .stream()
                                        .map(this::convertToResponseDTO)
                                        .toList();
                }

                // TECHNICIAN
                // can see only his assigned work orders

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        return workOrderRepository.findAll()
                                        .stream()
                                        .filter(workOrder -> workOrder.getAssignedTo() != null
                                                        && workOrder.getAssignedTo()
                                                                        .getId()
                                                                        .equals(loggedInUser.getId()))
                                        .map(this::convertToResponseDTO)
                                        .toList();
                }

                // CUSTOMER
                // can see only his customer's work orders

                if ("CUSTOMER".equalsIgnoreCase(role)) {

                        if (loggedInUser.getCustomer() == null) {

                                throw new RuntimeException(
                                                "Customer is not linked with user");
                        }

                        Long customerId = loggedInUser.getCustomer().getId();

                        return workOrderRepository.findAll()
                                        .stream()
                                        .filter(workOrder -> workOrder.getCustomer() != null
                                                        && workOrder.getCustomer()
                                                                        .getId()
                                                                        .equals(customerId))
                                        .map(this::convertToResponseDTO)
                                        .toList();
                }

                throw new RuntimeException("Unauthorized access");
        }

        // =========================================================
        // GET WORK ORDERS BY TECHNICIAN
        // =========================================================

        public List<WorkOrderResponseDTO> getWorkOrdersByTechnician(Long userId) {

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // Technician can access only his own jobs

                if ("TECHNICIAN".equalsIgnoreCase(role)
                                && !loggedInUser.getId().equals(userId)) {

                        throw new RuntimeException(
                                        "Technician cannot access another technician's work orders");
                }

                // Only ADMIN, MANAGER and TECHNICIAN

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Unauthorized access");
                }

                return workOrderRepository.findAll()
                                .stream()
                                .filter(workOrder -> workOrder.getAssignedTo() != null
                                                && workOrder.getAssignedTo()
                                                                .getId()
                                                                .equals(userId))
                                .map(this::convertToResponseDTO)
                                .toList();
        }

        // =========================================================
        // GET WORK ORDER BY ID
        // =========================================================

        public WorkOrderResponseDTO getWorkOrderById(Long id) {

                WorkOrder workOrder = workOrderRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // Technician can access only assigned work order

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician cannot access another technician's work order");
                        }
                }

                // Customer can access only his own work order

                if ("CUSTOMER".equalsIgnoreCase(role)) {

                        if (loggedInUser.getCustomer() == null
                                        || workOrder.getCustomer() == null
                                        || !workOrder.getCustomer()
                                                        .getId()
                                                        .equals(loggedInUser.getCustomer().getId())) {

                                throw new RuntimeException(
                                                "Customer cannot access this work order");
                        }
                }

                // ADMIN, MANAGER, DISPATCHER,
                // TECHNICIAN and CUSTOMER

                if ("ADMIN".equalsIgnoreCase(role)
                                || "MANAGER".equalsIgnoreCase(role)
                                || "DISPATCHER".equalsIgnoreCase(role)
                                || "TECHNICIAN".equalsIgnoreCase(role)
                                || "CUSTOMER".equalsIgnoreCase(role)) {

                        return convertToResponseDTO(workOrder);
                }

                throw new RuntimeException("Unauthorized access");
        }

        // =========================================================
        // UPDATE WORK ORDER
        // =========================================================

        public WorkOrderResponseDTO updateWorkOrder(
                        Long id,
                        WorkOrderRequestDTO requestDTO) {

                WorkOrder workOrder = workOrderRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // Technician can update only assigned job

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician cannot update another technician's work order");
                        }
                }

                // Only ADMIN, MANAGER and TECHNICIAN

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Unauthorized access");
                }

                // Closed / Cancelled cannot be edited

                if ("CLOSED".equalsIgnoreCase(workOrder.getStatus())
                                || "CANCELLED".equalsIgnoreCase(
                                                workOrder.getStatus())) {

                        throw new RuntimeException(
                                        "Closed or cancelled work order cannot be updated");
                }

                workOrder.setCode(requestDTO.getCode());
                workOrder.setTitle(requestDTO.getTitle());
                workOrder.setDescription(requestDTO.getDescription());
                workOrder.setPriority(requestDTO.getPriority());
                workOrder.setSlaDueAt(requestDTO.getSlaDueAt());

                // ADMIN and MANAGER can change
                // customer, site and technician

                if ("ADMIN".equalsIgnoreCase(role)
                                || "MANAGER".equalsIgnoreCase(role)) {

                        if (requestDTO.getCustomerId() != null) {

                                Customer customer = customerRepository
                                                .findById(requestDTO.getCustomerId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Customer not found"));

                                workOrder.setCustomer(customer);
                        }

                        if (requestDTO.getSiteId() != null) {

                                Site site = siteRepository
                                                .findById(requestDTO.getSiteId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Site not found"));

                                workOrder.setSite(site);
                        }

                        if (requestDTO.getAssignedToId() != null) {

                                User technician = userRepository
                                                .findById(requestDTO.getAssignedToId())
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Assigned user not found"));

                                workOrder.setAssignedTo(technician);
                        }
                }

                WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);

                return convertToResponseDTO(updatedWorkOrder);
        }

        // =========================================================
        // DELETE WORK ORDER
        // =========================================================

        public void deleteWorkOrder(Long id) {

                WorkOrder workOrder = workOrderRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                workOrderRepository.delete(workOrder);
        }

        // =========================================================
        // CHANGE STATUS
        // =========================================================

        public WorkOrderResponseDTO changeStatus(
                        Long id,
                        WorkOrderStatusRequestDTO requestDTO) {

                WorkOrder workOrder = workOrderRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                String oldStatus = workOrder.getStatus();

                String newStatus = requestDTO.getStatus();

                // Technician can change only assigned job

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician cannot change status of another technician's job");
                        }

                        // Technician cannot close/cancel

                        if ("CLOSED".equalsIgnoreCase(newStatus)
                                        || "CANCELLED".equalsIgnoreCase(newStatus)) {

                                throw new RuntimeException(
                                                "Technician cannot close or cancel work order");
                        }
                }

                // Dispatcher cannot change status

                if ("DISPATCHER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Dispatcher cannot change work order status");
                }

                // Customer cannot change status

                if ("CUSTOMER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Customer cannot change work order status");
                }

                workOrder.setStatus(newStatus);

                WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

                // =====================================================
                // SAVE STATUS HISTORY
                // =====================================================

                WorkOrderStatusHistory history = new WorkOrderStatusHistory();

                history.setWorkOrder(savedWorkOrder);

                history.setFromStatus(oldStatus);

                history.setToStatus(newStatus);

                history.setChangedBy(loggedInUser);

                history.setChangedAt(LocalDateTime.now());

                history.setNote(null);

                workOrderStatusHistoryRepository.save(history);

                return convertToResponseDTO(savedWorkOrder);
        }

        // =========================================================
        // GET STATUS HISTORY
        // =========================================================

        public List<WorkOrderStatusHistoryResponseDTO> getWorkOrderStatusHistory(Long workOrderId) {

                WorkOrder workOrder = workOrderRepository
                                .findById(workOrderId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // Technician can see only assigned job history

                if ("TECHNICIAN".equalsIgnoreCase(role)) {

                        if (workOrder.getAssignedTo() == null
                                        || !workOrder.getAssignedTo()
                                                        .getId()
                                                        .equals(loggedInUser.getId())) {

                                throw new RuntimeException(
                                                "Technician cannot access another technician's history");
                        }
                }

                // Customer can see only own customer's history

                if ("CUSTOMER".equalsIgnoreCase(role)) {

                        if (loggedInUser.getCustomer() == null
                                        || workOrder.getCustomer() == null
                                        || !workOrder.getCustomer()
                                                        .getId()
                                                        .equals(loggedInUser.getCustomer().getId())) {

                                throw new RuntimeException(
                                                "Customer cannot access this work order history");
                        }
                }

                // ADMIN, MANAGER, DISPATCHER,
                // TECHNICIAN and CUSTOMER

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"DISPATCHER".equalsIgnoreCase(role)
                                && !"TECHNICIAN".equalsIgnoreCase(role)
                                && !"CUSTOMER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Unauthorized access");
                }

                return workOrderStatusHistoryRepository
                                .findByWorkOrderIdOrderByChangedAtAsc(workOrderId)
                                .stream()
                                .map(this::convertHistoryToResponseDTO)
                                .toList();
        }

        // =========================================================
        // ASSIGN WORK ORDER
        // =========================================================

        public WorkOrderResponseDTO assignWorkOrder(
                        Long id,
                        WorkOrderAssignRequestDTO requestDTO) {

                WorkOrder workOrder = workOrderRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Work Order not found"));

                User loggedInUser = getLoggedInUser();

                String role = loggedInUser.getRole().toUpperCase();

                // ADMIN, MANAGER and DISPATCHER
                // can assign work order

                if (!"ADMIN".equalsIgnoreCase(role)
                                && !"MANAGER".equalsIgnoreCase(role)
                                && !"DISPATCHER".equalsIgnoreCase(role)) {

                        throw new RuntimeException(
                                        "Only Admin, Manager or Dispatcher can assign work orders");
                }

                // IMPORTANT:
                // DTO field is technicianId

                User technician = userRepository
                                .findById(requestDTO.getTechnicianId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Technician not found"));

                // User must actually be TECHNICIAN

                if (!"TECHNICIAN".equalsIgnoreCase(
                                technician.getRole())) {

                        throw new RuntimeException(
                                        "Work order can only be assigned to a technician");
                }

                // Closed / Cancelled cannot be assigned

                if ("CLOSED".equalsIgnoreCase(workOrder.getStatus())
                                || "CANCELLED".equalsIgnoreCase(
                                                workOrder.getStatus())) {

                        throw new RuntimeException(
                                        "Closed or cancelled work order cannot be assigned");
                }

                workOrder.setAssignedTo(technician);

                WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

                return convertToResponseDTO(savedWorkOrder);
        }

        // =========================================================
        // GET LOGGED IN USER
        // =========================================================

        private User getLoggedInUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()) {

                        throw new RuntimeException(
                                        "User not authenticated");
                }

                String principal = authentication.getName();

                // JWT principal format:
                // userId|email

                if (principal == null
                                || !principal.contains("|")) {

                        throw new RuntimeException(
                                        "Invalid authentication information");
                }

                String[] parts = principal.split("\\|");

                Long userId;

                try {

                        userId = Long.parseLong(parts[0]);

                } catch (NumberFormatException e) {

                        throw new RuntimeException(
                                        "Invalid user ID in authentication");
                }

                return userRepository
                                .findById(userId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Logged in user not found"));
        }

        // =========================================================
        // WORK ORDER -> RESPONSE DTO
        // =========================================================

        private WorkOrderResponseDTO convertToResponseDTO(
                        WorkOrder workOrder) {

                WorkOrderResponseDTO responseDTO = new WorkOrderResponseDTO();

                responseDTO.setId(workOrder.getId());

                responseDTO.setCode(workOrder.getCode());

                responseDTO.setTitle(workOrder.getTitle());

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
        // HISTORY -> RESPONSE DTO
        // =========================================================

        private WorkOrderStatusHistoryResponseDTO convertHistoryToResponseDTO(
                        WorkOrderStatusHistory history) {

                WorkOrderStatusHistoryResponseDTO responseDTO = new WorkOrderStatusHistoryResponseDTO();

                responseDTO.setId(history.getId());

                responseDTO.setWorkOrderId(
                                history.getWorkOrder().getId());

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