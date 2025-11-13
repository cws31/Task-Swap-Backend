package com.TaskSwap.services;

import com.TaskSwap.DTOs.TaskRequestDTO;
import com.TaskSwap.entities.*;
import com.TaskSwap.enums.*;
import com.TaskSwap.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskRequestService {

        private final TaskRequestRepository taskRequestRepository;
        private final TaskRepository taskRepository;
        private final SkillRepository skillRepository;
        private final NotificationService notificationService;

        public TaskRequestDTO createRequest(User requester, Long targetId, RequestType type) {
                TaskRequest request = new TaskRequest();
                request.setRequester(requester);
                request.setRequestType(type);
                request.setCreatedAt(LocalDateTime.now());
                request.setStatus(RequestStatus.PENDING);

                if (type == RequestType.TASK) {
                        Task task = taskRepository.findById(targetId)
                                        .orElseThrow(() -> new RuntimeException("Task not found"));

                        if (task.getCreatedBy().getId().equals(requester.getId())) {
                                throw new RuntimeException("You cannot request your own task");
                        }

                        Optional<TaskRequest> latestRequest = taskRequestRepository
                                        .findTopByRequesterAndTaskOrderByCreatedAtDesc(requester, task);

                        if (latestRequest.isPresent() && latestRequest.get().getStatus() == RequestStatus.PENDING) {
                                throw new RuntimeException("You have already requested this task");
                        }

                        request.setTask(task);
                        request.setReceiver(task.getCreatedBy());
                        taskRequestRepository.save(request);

                        notificationService.sendNotification(
                                        task.getCreatedBy(),
                                        requester.getFullName() + " requested your task '" + task.getTitle() + "'",
                                        request);

                        return toDTO(request);

                } else if (type == RequestType.SKILL) {
                        Skill skill = skillRepository.findById(targetId)
                                        .orElseThrow(() -> new RuntimeException("Skill not found"));

                        if (skill.getUser().getId().equals(requester.getId())) {
                                throw new RuntimeException("You cannot request your own skill");
                        }

                        Optional<TaskRequest> latestRequest = taskRequestRepository
                                        .findTopByRequesterAndSkillOrderByCreatedAtDesc(requester, skill);

                        if (latestRequest.isPresent() && latestRequest.get().getStatus() == RequestStatus.PENDING) {
                                throw new RuntimeException("You have already requested this skill");
                        }

                        request.setSkill(skill);
                        request.setReceiver(skill.getUser());
                        taskRequestRepository.save(request);

                        notificationService.sendNotification(
                                        skill.getUser(),
                                        requester.getFullName() + " requested your skill '" + skill.getTitle() + "'",
                                        request);

                        return toDTO(request);
                }

                throw new RuntimeException("Invalid request type");
        }

        public TaskRequestDTO respondToRequest(Long requestId, boolean accept) {
                TaskRequest req = taskRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                String title = req.getRequestType() == RequestType.TASK ? req.getTask().getTitle()
                                : req.getSkill().getTitle();

                String message = "Your request for '" + title + "' has been " + (accept ? "accepted" : "rejected");
                notificationService.sendNotification(req.getRequester(), message, req);

                req.setStatus(accept ? RequestStatus.ACCEPTED : RequestStatus.REJECTED);
                taskRequestRepository.save(req);

                return toDTO(req);
        }

        public Map<String, List<TaskRequestDTO>> getAllRequestsForUser(User user) {
                List<TaskRequestDTO> received = taskRequestRepository.findByReceiver(user)
                                .stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());

                List<TaskRequestDTO> sent = taskRequestRepository.findByRequester(user)
                                .stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList());

                Map<String, List<TaskRequestDTO>> result = new HashMap<>();
                result.put("receivedRequests", received);
                result.put("sentRequests", sent);
                return result;
        }

        private TaskRequestDTO toDTO(TaskRequest req) {
                String title = req.getRequestType() == RequestType.TASK
                                ? req.getTask().getTitle()
                                : req.getSkill().getTitle();

                return new TaskRequestDTO(
                                req.getId(),
                                req.getRequester().getUsername(),
                                req.getReceiver().getUsername(),
                                req.getRequester().getFullName(),
                                req.getReceiver().getFullName(),
                                title,
                                req.getRequestType(),
                                req.getStatus(),
                                req.getCreatedAt());
        }
}
