package com.fieldservicemanagement.fieldservicemanagement.repository;

import com.fieldservicemanagement.fieldservicemanagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
                extends JpaRepository<Notification, Long> {

        List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

        List<Notification> findByUserIdAndReadStatusOrderByCreatedAtDesc(
                        Long userId,
                        boolean readStatus);

        boolean existsByUser_IdAndWorkOrder_IdAndMessage(
                        Long userId,
                        Long workOrderId,
                        String message);
}