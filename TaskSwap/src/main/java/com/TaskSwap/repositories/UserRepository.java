package com.TaskSwap.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TaskSwap.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
