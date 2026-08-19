package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.NotificationResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Notification;
import com.fieldservicemanagement.fieldservicemanagement.entity.User;
import com.fieldservicemanagement.fieldservicemanagement.entity.WorkOrder;
import com.fieldservicemanagement.fieldservicemanagement.repository.NotificationRepository;
import com.fieldservicemanagement.fieldservicemanagement.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

        private final NotificationRepository notificationRepository;
        private final UserRepository userRepository;

        public NotificationService(
                        NotificationRepository notificationRepository,
                        UserRepository userRepository) {

                this.notificationRepository = notificationRepository;
                this.userRepository = userRepository;
        }

        // =========================================================
        // CREATE SLA BREACH NOTIFICATION
        // =========================================================

        @Transactional
        public void createSlaBreachNotification(
                        WorkOrder workOrder) {

                // Saare users me se sirf MANAGER ko find karo
                List<User> managers = userRepository.findAll()
                                .stream()
                                .filter(user -> "MANAGER".equalsIgnoreCase(
                                                user.getRole()))
                                .toList();

                // Notification message
                String message = "SLA breached for Work Order "
                                + workOrder.getCode();

                // Har manager ke liye notification
                for (User manager : managers) {

                        // Check karo ki same notification
                        // pehle se database me exist karti hai ya nahi
                        boolean alreadyExists = notificationRepository
                                        .existsByUser_IdAndWorkOrder_IdAndMessage(
                                                        manager.getId(),
                                                        workOrder.getId(),
                                                        message);

                        // Agar already exist karti hai
                        // to duplicate notification mat banao
                        if (alreadyExists) {
                                continue;
                        }

                        // New notification create karo
                        Notification notification = new Notification();

                        notification.setUser(manager);

                        notification.setWorkOrder(workOrder);

                        notification.setMessage(message);

                        notification.setReadStatus(false);

                        notification.setCreatedAt(
                                        LocalDateTime.now());

                        // Database me save karo
                        notificationRepository.save(
                                        notification);
                }
        }

        // =========================================================
        // GET USER NOTIFICATIONS
        // =========================================================

        public List<NotificationResponseDTO> getUserNotifications(
                        Long userId) {

                return notificationRepository
                                .findByUserIdOrderByCreatedAtDesc(userId)
                                .stream()
                                .map(this::convertToResponseDTO)
                                .toList();
        }

        // =========================================================
        // GET UNREAD NOTIFICATIONS
        // =========================================================

        public List<NotificationResponseDTO> getUnreadNotifications(
                        Long userId) {

                return notificationRepository
                                .findByUserIdAndReadStatusOrderByCreatedAtDesc(
                                                userId,
                                                false)
                                .stream()
                                .map(this::convertToResponseDTO)
                                .toList();
        }

        // =========================================================
        // MARK NOTIFICATION AS READ
        // =========================================================

        @Transactional
        public NotificationResponseDTO markAsRead(
                        Long notificationId) {

                Notification notification = notificationRepository
                                .findById(notificationId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Notification not found with id: "
                                                                + notificationId));

                notification.setReadStatus(true);

                Notification saved = notificationRepository.save(
                                notification);

                return convertToResponseDTO(saved);
        }

        // =========================================================
        // ENTITY → RESPONSE DTO
        // =========================================================

        private NotificationResponseDTO convertToResponseDTO(
                        Notification notification) {

                NotificationResponseDTO responseDTO = new NotificationResponseDTO();

                responseDTO.setId(
                                notification.getId());

                responseDTO.setUserId(
                                notification.getUser().getId());

                responseDTO.setWorkOrderId(
                                notification.getWorkOrder().getId());

                responseDTO.setMessage(
                                notification.getMessage());

                responseDTO.setReadStatus(
                                notification.isReadStatus());

                responseDTO.setCreatedAt(
                                notification.getCreatedAt());

                return responseDTO;
        }
}