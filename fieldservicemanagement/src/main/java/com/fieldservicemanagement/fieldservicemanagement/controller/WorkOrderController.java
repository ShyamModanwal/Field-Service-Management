package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderAssignRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusHistoryResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.WorkOrderService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@SecurityRequirement(name = "bearerAuth")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // =========================================================
    // CREATE WORK ORDER
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISPATCHER')")
    public WorkOrderResponseDTO createWorkOrder(
            @RequestBody WorkOrderRequestDTO requestDTO) {

        return workOrderService.saveWorkOrder(requestDTO);
    }

    // =========================================================
    // GET ALL WORK ORDERS
    // ADMIN / MANAGER / DISPATCHER / TECHNICIAN / CUSTOMER
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISPATCHER', 'TECHNICIAN', 'CUSTOMER')")
    public List<WorkOrderResponseDTO> getAllWorkOrders() {

        return workOrderService.getAllWorkOrders();
    }

    // =========================================================
    // GET WORK ORDERS BY TECHNICIAN
    // =========================================================

    @GetMapping("/technician/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public List<WorkOrderResponseDTO> getWorkOrdersByTechnician(
            @PathVariable Long userId) {

        return workOrderService.getWorkOrdersByTechnician(userId);
    }

    // =========================================================
    // GET WORK ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISPATCHER', 'TECHNICIAN', 'CUSTOMER')")
    public WorkOrderResponseDTO getWorkOrderById(
            @PathVariable Long id) {

        return workOrderService.getWorkOrderById(id);
    }

    // =========================================================
    // UPDATE WORK ORDER
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public WorkOrderResponseDTO updateWorkOrder(
            @PathVariable Long id,
            @RequestBody WorkOrderRequestDTO requestDTO) {

        return workOrderService.updateWorkOrder(
                id,
                requestDTO);
    }

    // =========================================================
    // DELETE WORK ORDER
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteWorkOrder(
            @PathVariable Long id) {

        workOrderService.deleteWorkOrder(id);

        return "Work Order deleted successfully";
    }

    // =========================================================
    // CHANGE WORK ORDER STATUS
    // =========================================================

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER', 'ADMIN')")
    public WorkOrderResponseDTO changeStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusRequestDTO requestDTO) {

        return workOrderService.changeStatus(
                id,
                requestDTO);
    }

    // =========================================================
    // GET WORK ORDER STATUS HISTORY
    // =========================================================

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISPATCHER', 'TECHNICIAN', 'CUSTOMER')")
    public List<WorkOrderStatusHistoryResponseDTO> getWorkOrderStatusHistory(
            @PathVariable Long id) {

        return workOrderService.getWorkOrderStatusHistory(id);
    }

    // =========================================================
    // ASSIGN WORK ORDER TO TECHNICIAN
    // =========================================================

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DISPATCHER')")
    public WorkOrderResponseDTO assignWorkOrder(
            @PathVariable Long id,
            @RequestBody WorkOrderAssignRequestDTO requestDTO) {

        return workOrderService.assignWorkOrder(
                id,
                requestDTO);
    }
}