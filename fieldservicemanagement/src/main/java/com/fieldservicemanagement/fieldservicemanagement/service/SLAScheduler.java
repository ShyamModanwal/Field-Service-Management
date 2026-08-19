package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SLAScheduler {

    private final WorkOrderRepository workOrderRepository;
    private final NotificationService notificationService;

    public SLAScheduler(
            WorkOrderRepository workOrderRepository,
            NotificationService notificationService) {

        this.workOrderRepository = workOrderRepository;
        this.notificationService = notificationService;
    }

    // =========================================================
    // AUTOMATIC SLA CHECK
    // =========================================================

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkSLA() {

        List<WorkOrder> workOrders = workOrderRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (WorkOrder workOrder : workOrders) {

            // SLA not configured
            if (workOrder.getSlaDueAt() == null) {
                continue;
            }

            // Completed work order
            if ("COMPLETED".equalsIgnoreCase(
                    workOrder.getStatus())) {

                continue;
            }

            // Cancelled work order
            if ("CANCELLED".equalsIgnoreCase(
                    workOrder.getStatus())) {

                continue;
            }

            LocalDateTime dueTime = LocalDateTime.parse(
                    workOrder.getSlaDueAt());

            // =================================================
            // SLA BREACHED
            // =================================================

            if (now.isAfter(dueTime)) {

                System.out.println(
                        "SLA BREACHED: Work Order "
                                + workOrder.getCode());

                notificationService
                        .createSlaBreachNotification(
                                workOrder);
            }
        }
    }
}