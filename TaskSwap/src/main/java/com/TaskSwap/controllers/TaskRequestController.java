package com.TaskSwap.controllers;

import com.TaskSwap.DTOs.TaskRequestDTO;
import com.TaskSwap.entities.TaskRequest;
import com.TaskSwap.entities.User;
import com.TaskSwap.enums.RequestType;
import com.TaskSwap.enums.RequestStatus;
import com.TaskSwap.repositories.TaskRequestRepository;
import com.TaskSwap.repositories.UserRepository;
import com.TaskSwap.repositories.TaskRepository;
import com.TaskSwap.repositories.SkillRepository;
import com.TaskSwap.security.JwtUtil;
import com.TaskSwap.services.TaskRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class TaskRequestController {

    private final TaskRequestService taskRequestService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TaskRequestRepository taskRequestRepository;
    private final TaskRepository taskRepository;
    private final SkillRepository skillRepository;

    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        return header.substring(7).trim();
    }

    private User getUserFromHeader(String authHeader) {
        String token = extractToken(authHeader);
        String username = jwtUtil.getUsernameFromToken(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> requestTask(@RequestHeader("Authorization") String authHeader,
            @PathVariable Long taskId) {
        try {
            User requester = getUserFromHeader(authHeader);
            TaskRequestDTO dto = taskRequestService.createRequest(requester, taskId, RequestType.TASK);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/skill/{skillId}")
    public ResponseEntity<?> requestSkill(@RequestHeader("Authorization") String authHeader,
            @PathVariable Long skillId) {
        try {
            User requester = getUserFromHeader(authHeader);
            TaskRequestDTO dto = taskRequestService.createRequest(requester, skillId, RequestType.SKILL);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {
        try {
            TaskRequestDTO dto = taskRequestService.respondToRequest(id, true);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            TaskRequestDTO dto = taskRequestService.respondToRequest(id, false);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRequests(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getUserFromHeader(authHeader);
            Map<String, Object> result = Map.of(
                    "requests", taskRequestService.getAllRequestsForUser(user));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}/status")
    public ResponseEntity<Map<String, String>> getTaskRequestStatus(@RequestHeader("Authorization") String authHeader,
            @PathVariable Long taskId) {
        User user = getUserFromHeader(authHeader);

        Optional<TaskRequest> requestOpt = taskRequestRepository
                .findByRequesterAndTask(user, taskRepository.findById(taskId)
                        .orElseThrow(() -> new RuntimeException("Task not found")));

        if (requestOpt.isPresent()) {
            TaskRequest req = requestOpt.get();
            Map<String, String> response = new HashMap<>();
            response.put("status", req.getStatus().name());
            response.put("message", switch (req.getStatus()) {
                case PENDING -> "Request Sent ";
                case ACCEPTED -> "Request Accepted ";
                case REJECTED -> "Request Rejected ";
            });
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(Map.of("status", "NONE", "message", ""));
    }

    @GetMapping("/skill/{skillId}/status")
    public ResponseEntity<Map<String, String>> getSkillRequestStatus(@RequestHeader("Authorization") String authHeader,
            @PathVariable Long skillId) {
        User user = getUserFromHeader(authHeader);

        Optional<TaskRequest> requestOpt = taskRequestRepository
                .findByRequesterAndSkill(user, skillRepository.findById(skillId)
                        .orElseThrow(() -> new RuntimeException("Skill not found")));

        if (requestOpt.isPresent()) {
            TaskRequest req = requestOpt.get();
            Map<String, String> response = new HashMap<>();
            response.put("status", req.getStatus().name());
            response.put("message", switch (req.getStatus()) {
                case PENDING -> "Request Sent ";
                case ACCEPTED -> "Request Accepted ";
                case REJECTED -> "Request Rejected ";
            });
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(Map.of("status", "NONE", "message", ""));
    }
}
