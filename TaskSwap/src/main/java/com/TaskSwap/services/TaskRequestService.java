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
        private final UserRepository userRepository;
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

                        boolean alreadyRequested = taskRequestRepository
                                        .existsByRequesterAndTaskAndRequestType(requester, task, RequestType.TASK);
                        if (alreadyRequested) {
                                throw new RuntimeException("You have already requested this task");
                        }

                        request.setTask(task);
                        request.setReceiver(task.getCreatedBy());
                        taskRequestRepository.save(request);

                        notificationService.sendNotification(
                                        task.getCreatedBy(),
                                        requester.getFullName() + " requested your task '" + task.getTitle() + "'",
                                        request);

                } else if (type == RequestType.SKILL) {
                        Skill skill = skillRepository.findById(targetId)
                                        .orElseThrow(() -> new RuntimeException("Skill not found"));

                        if (skill.getUser().getId().equals(requester.getId())) {
                                throw new RuntimeException("You cannot request your own skill");
                        }

                        boolean alreadyRequested = taskRequestRepository
                                        .existsByRequesterAndSkillAndRequestType(requester, skill, RequestType.SKILL);
                        if (alreadyRequested) {
                                throw new RuntimeException("You have already requested this skill");
                        }

                        request.setSkill(skill);
                        request.setReceiver(skill.getUser());
                        taskRequestRepository.save(request);

                        notificationService.sendNotification(
                                        skill.getUser(),
                                        requester.getFullName() + " requested your skill '" + skill.getTitle() + "'",
                                        request);
                }

                return new TaskRequestDTO(
                                request.getId(),
                                requester.getUsername(),
                                request.getReceiver().getUsername(),
                                request.getRequestType(),
                                request.getStatus(),
                                request.getCreatedAt());
        }

        public TaskRequestDTO respondToRequest(Long requestId, boolean accept) {
                TaskRequest req = taskRequestRepository.findById(requestId)
                                .orElseThrow(() -> new RuntimeException("Request not found"));

                req.setStatus(accept ? RequestStatus.ACCEPTED : RequestStatus.REJECTED);
                taskRequestRepository.save(req);

                String message = "Your request for '" +
                                (req.getRequestType() == RequestType.TASK
                                                ? req.getTask().getTitle()
                                                : req.getSkill().getTitle())
                                + "' has been " + (accept ? "accepted" : "rejected");

                notificationService.sendNotification(req.getRequester(), message, req);

                return new TaskRequestDTO(
                                req.getId(),
                                req.getRequester().getUsername(),
                                req.getReceiver().getUsername(),
                                req.getRequestType(),
                                req.getStatus(),
                                req.getCreatedAt());
        }

        public Map<String, List<TaskRequestDTO>> getAllRequestsForUser(User user) {

                List<TaskRequestDTO> received = taskRequestRepository.findByReceiver(user)
                                .stream()
                                .map(req -> new TaskRequestDTO(
                                                req.getId(),
                                                req.getRequester().getUsername(),
                                                req.getReceiver().getUsername(),
                                                req.getRequestType(),
                                                req.getStatus(),
                                                req.getCreatedAt()))
                                .collect(Collectors.toList());

                List<TaskRequestDTO> sent = taskRequestRepository.findByRequester(user)
                                .stream()
                                .map(req -> new TaskRequestDTO(
                                                req.getId(),
                                                req.getRequester().getUsername(),
                                                req.getReceiver().getUsername(),
                                                req.getRequestType(),
                                                req.getStatus(),
                                                req.getCreatedAt()))
                                .collect(Collectors.toList());

                Map<String, List<TaskRequestDTO>> result = new HashMap<>();
                result.put("receivedRequests", received);
                result.put("sentRequests", sent);

                return result;
        }
}
