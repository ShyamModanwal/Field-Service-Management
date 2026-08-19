package com.fieldservicemanagement.fieldservicemanagement.repository;

import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderStatusHistoryRepository
                extends JpaRepository<WorkOrderStatusHistory, Long> {

        List<WorkOrderStatusHistory> findByWorkOrderIdOrderByChangedAtAsc(Long workOrderId);
}