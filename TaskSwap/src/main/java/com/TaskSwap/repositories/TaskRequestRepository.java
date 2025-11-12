package com.TaskSwap.repositories;

import com.TaskSwap.entities.*;
import com.TaskSwap.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRequestRepository extends JpaRepository<TaskRequest, Long> {

    List<TaskRequest> findByRequester(User requester);

    List<TaskRequest> findByReceiver(User receiver);

    List<TaskRequest> findByTask_CreatedBy(User owner);

    List<TaskRequest> findBySkill_User(User owner);

    boolean existsByRequesterAndTaskAndRequestType(User requester, Task task, RequestType type);

    boolean existsByRequesterAndSkillAndRequestType(User requester, Skill skill, RequestType type);

    Optional<TaskRequest> findByRequesterAndTask(User requester, Task task);

    Optional<TaskRequest> findByRequesterAndSkill(User requester, Skill skill);
}
