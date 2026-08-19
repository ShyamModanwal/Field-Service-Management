package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.DashboardResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final WorkOrderRepository workOrderRepository;

    public DashboardService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    // =========================================================
    // GET DASHBOARD SUMMARY
    // =========================================================

    public DashboardResponseDTO getDashboardSummary() {

        List<WorkOrder> workOrders = workOrderRepository.findAll();

        DashboardResponseDTO responseDTO = new DashboardResponseDTO();

        // Total Work Orders
        responseDTO.setTotalWorkOrders(workOrders.size());

        // Count according to status
        responseDTO.setNewWorkOrders(
                countByStatus(workOrders, "NEW"));

        responseDTO.setAssignedWorkOrders(
                countByStatus(workOrders, "ASSIGNED"));

        responseDTO.setInProgressWorkOrders(
                countByStatus(workOrders, "IN_PROGRESS"));

        responseDTO.setOnHoldWorkOrders(
                countByStatus(workOrders, "ON_HOLD"));

        responseDTO.setCompletedWorkOrders(
                countByStatus(workOrders, "COMPLETED"));

        responseDTO.setClosedWorkOrders(
                countByStatus(workOrders, "CLOSED"));

        responseDTO.setCancelledWorkOrders(
                countByStatus(workOrders, "CANCELLED"));

        return responseDTO;
    }

    // =========================================================
    // COUNT WORK ORDERS BY STATUS
    // =========================================================

    private long countByStatus(
            List<WorkOrder> workOrders,
            String status) {

        return workOrders.stream()
                .filter(workOrder -> status.equalsIgnoreCase(
                        workOrder.getStatus()))
                .count();
    }
}