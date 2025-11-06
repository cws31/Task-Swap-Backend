package com.TaskSwap.repositories;

import com.TaskSwap.entities.Skill;
import com.TaskSwap.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUser(User user);
}
