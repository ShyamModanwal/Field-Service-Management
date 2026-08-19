package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.WorkOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class SLAService {

    private final WorkOrderRepository workOrderRepository;

    public SLAService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    // =========================================================
    // CALCULATE SLA DUE TIME
    // =========================================================

    @Transactional
    public WorkOrder calculateSla(Long workOrderId) {

        WorkOrder workOrder = workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "Work Order not found with id: "
                                + workOrderId));

        /*
         * SLA is calculated according to priority.
         *
         * HIGH -> 4 hours
         * MEDIUM -> 8 hours
         * LOW -> 24 hours
         */

        String priority = workOrder.getPriority();

        long slaHours;

        if ("HIGH".equalsIgnoreCase(priority)) {

            slaHours = 4;

        } else if ("MEDIUM".equalsIgnoreCase(priority)) {

            slaHours = 8;

        } else if ("LOW".equalsIgnoreCase(priority)) {

            slaHours = 24;

        } else {

            throw new RuntimeException(
                    "Invalid priority: " + priority);
        }

        LocalDateTime dueTime = LocalDateTime.now()
                .plusHours(slaHours);

        workOrder.setSlaDueAt(
                dueTime.toString());

        return workOrderRepository.save(workOrder);
    }

    // =========================================================
    // CHECK SLA STATUS
    // =========================================================

    public String getSlaStatus(Long workOrderId) {

        WorkOrder workOrder = workOrderRepository
                .findById(workOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "Work Order not found with id: "
                                + workOrderId));

        if (workOrder.getSlaDueAt() == null) {

            return "SLA_NOT_SET";
        }

        LocalDateTime dueTime = LocalDateTime.parse(
                workOrder.getSlaDueAt());

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(dueTime)) {

            return "BREACHED";
        }

        Duration remaining = Duration.between(now, dueTime);

        long hours = remaining.toHours();

        long minutes = remaining.toMinutesPart();

        return "WITHIN_SLA - "
                + hours
                + " hours "
                + minutes
                + " minutes remaining";
    }
}