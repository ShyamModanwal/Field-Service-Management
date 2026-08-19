package com.fieldservicemanagement.fieldservicemanagement.controller;

import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.service.WorkOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderAssignRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.WorkOrderStatusHistoryResponseDTO;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/work-orders")
@SecurityRequirement(name = "bearerAuth")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // POST - Create Work Order
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public WorkOrderResponseDTO createWorkOrder(
            @RequestBody WorkOrderRequestDTO requestDTO) {

        return workOrderService.saveWorkOrder(requestDTO);
    }

    // GET - Get All Work Orders
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public List<WorkOrderResponseDTO> getAllWorkOrders() {

        return workOrderService.getAllWorkOrders();
    }

    // GET - Work Orders By Technician
    @GetMapping("/technician/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public List<WorkOrderResponseDTO> getWorkOrdersByTechnician(
            @PathVariable Long userId) {

        return workOrderService.getWorkOrdersByTechnician(userId);
    }

    // GET - Get Work Order By ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public WorkOrderResponseDTO getWorkOrderById(
            @PathVariable Long id) {

        return workOrderService.getWorkOrderById(id);
    }

    // PUT - Update Work Order
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public WorkOrderResponseDTO updateWorkOrder(
            @PathVariable Long id,
            @RequestBody WorkOrderRequestDTO requestDTO) {

        return workOrderService.updateWorkOrder(id, requestDTO);
    }

    // DELETE - Delete Work Order
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteWorkOrder(@PathVariable Long id) {

        workOrderService.deleteWorkOrder(id);

        return "Work Order deleted successfully";
    }

    // POST - Change Work Order Status
    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER', 'ADMIN')")
    public WorkOrderResponseDTO changeStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusRequestDTO requestDTO) {

        return workOrderService.changeStatus(id, requestDTO);
    }

    // GET - Work Order Status History
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECHNICIAN')")
    public List<WorkOrderStatusHistoryResponseDTO> getWorkOrderStatusHistory(
            @PathVariable Long id) {

        return workOrderService.getWorkOrderStatusHistory(id);
    }

    // POST - Assign Work Order To Technician
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