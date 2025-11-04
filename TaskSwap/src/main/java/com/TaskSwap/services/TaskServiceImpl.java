package com.TaskSwap.services;

import com.TaskSwap.DTOs.TaskRequest;
import com.TaskSwap.DTOs.TaskResponse;
import com.TaskSwap.DTOs.TaskUpdateRequest;
import com.TaskSwap.entities.Task;
import com.TaskSwap.entities.User;
import com.TaskSwap.repositories.TaskRepository;
import com.TaskSwap.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .rewardType(request.getRewardType())
                .rewardAmount(request.getRewardAmount())
                .preferredSwapSkill(request.getPreferredSwapSkill())
                .createdBy(user)
                .build();

        Task saved = taskRepository.save(task);

        return mapToResponse(saved);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByCreatedBy(user)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .category(task.getCategory())
                .rewardType(task.getRewardType())
                .rewardAmount(task.getRewardAmount())
                .preferredSwapSkill(task.getPreferredSwapSkill())
                .status(task.getStatus())
                .createdBy(task.getCreatedBy().getUsername())
                .createdAt(task.getCreatedAt())
                .build();
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());
        if (request.getCategory() != null)
            task.setCategory(request.getCategory());
        if (request.getRewardType() != null)
            task.setRewardType(request.getRewardType());
        if (request.getRewardAmount() != null)
            task.setRewardAmount(request.getRewardAmount());
        if (request.getPreferredSwapSkill() != null)
            task.setPreferredSwapSkill(request.getPreferredSwapSkill());

        Task updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    @Override
    public void deleteTask(Long taskId, String username) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getCreatedBy().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this task");
        }

        taskRepository.delete(task);
    }

}
