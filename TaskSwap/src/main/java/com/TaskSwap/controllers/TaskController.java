package com.TaskSwap.controllers;

import com.TaskSwap.DTOs.TaskRequest;
import com.TaskSwap.DTOs.TaskResponse;
import com.TaskSwap.DTOs.TaskUpdateRequest;
import com.TaskSwap.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(taskService.createTask(request, username));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(taskService.getTasksByUser(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(id, request, username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        taskService.deleteTask(id, username);
        return ResponseEntity.ok("Task deleted successfully");
    }

}
