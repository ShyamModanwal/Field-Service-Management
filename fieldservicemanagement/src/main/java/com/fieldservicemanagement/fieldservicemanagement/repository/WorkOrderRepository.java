package com.fieldservicemanagement.fieldservicemanagement.repository;

import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    // Technician ke assigned work orders
    List<WorkOrder> findByAssignedToId(Long userId);

    // Customer ke sirf apne work orders
    List<WorkOrder> findByCustomerId(Long customerId);
}