package com.TaskSwap.controllers;

import com.TaskSwap.DTOs.NotificationDTO;
import com.TaskSwap.entities.Notification;
import com.TaskSwap.entities.User;
import com.TaskSwap.repositories.NotificationRepository;
import com.TaskSwap.repositories.UserRepository;
import com.TaskSwap.security.JwtUtil;
import com.TaskSwap.services.NotificationService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        List<Notification> notifications = notificationRepository.findByReceiverOrderByCreatedAtDesc(user);

        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(notificationService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificationDTOs);
    }

    @PostMapping("/mark-read/{id}")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok(notificationService.convertToDTO(notification));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Access denied");
        }

        notificationRepository.delete(notification);
        return ResponseEntity.ok("Notification deleted");
    }

    private User getUserFromToken(String authHeader) {
        String token = extractToken(authHeader);
        String username = jwtUtil.getUsernameFromToken(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        throw new RuntimeException("Invalid Authorization header");
    }

    @GetMapping("/count")
    public ResponseEntity<?> getMyNotificationCount(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);

        Long unreadCount = notificationRepository.countByReceiverAndIsReadFalse(user);
        Long totalCount = notificationRepository.countByReceiver(user);

        return ResponseEntity.ok(Map.of(
                "unread", unreadCount,
                "total", totalCount));
    }

}
