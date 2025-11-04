package com.TaskSwap.repositories;

import com.TaskSwap.entities.Task;
import com.TaskSwap.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCreatedBy(User user);
}
