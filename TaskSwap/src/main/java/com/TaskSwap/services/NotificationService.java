package com.TaskSwap.services;

import com.TaskSwap.DTOs.NotificationDTO;
import com.TaskSwap.DTOs.TaskRequestDTO;
import com.TaskSwap.entities.Notification;
import com.TaskSwap.entities.TaskRequest;
import com.TaskSwap.entities.User;
import com.TaskSwap.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

        private final NotificationRepository notificationRepository;
        private final SimpMessagingTemplate messagingTemplate;

        public NotificationDTO sendNotification(User receiver, String message, TaskRequest taskRequest) {
                Notification notification = Notification.builder()
                                .receiver(receiver)
                                .message(message)
                                .taskRequest(taskRequest)
                                .isRead(false)
                                .createdAt(LocalDateTime.now())
                                .build();

                notificationRepository.save(notification);

                NotificationDTO dto = convertToDTO(notification);

                messagingTemplate.convertAndSend(
                                "/topic/user/" + receiver.getUsername(),
                                dto);

                return dto;
        }

        public NotificationDTO convertToDTO(Notification notification) {
                TaskRequestDTO trDto = null;
                if (notification.getTaskRequest() != null) {
                        TaskRequest tr = notification.getTaskRequest();
                        trDto = new TaskRequestDTO(
                                        tr.getId(),
                                        tr.getRequester().getUsername(),
                                        tr.getReceiver().getUsername(),
                                        tr.getRequestType(),
                                        tr.getStatus(),
                                        tr.getCreatedAt());
                }

                return new NotificationDTO(
                                notification.getId(),
                                notification.getMessage(),
                                notification.isRead(),
                                notification.getCreatedAt(),
                                notification.getReceiver() != null ? notification.getReceiver().getUsername() : null,
                                trDto);
        }
}
