package com.TaskSwap.services;

import com.TaskSwap.DTOs.TaskRequest;
import com.TaskSwap.DTOs.TaskResponse;
import com.TaskSwap.DTOs.TaskUpdateRequest;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request, String username);

    List<TaskResponse> getAllTasks();

    List<TaskResponse> getTasksByUser(String username);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest request, String username);

    void deleteTask(Long taskId, String username);

}
