package com.TaskSwap.repositories;

import com.TaskSwap.entities.Notification;
import com.TaskSwap.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);

    List<Notification> findByReceiverUsername(String username);

    Optional<Notification> findTopByReceiverOrderByCreatedAtDesc(User receiver);

    Long countByReceiverAndIsReadFalse(User receiver); // 👈 unread notifications count

    Long countByReceiver(User receiver);
}
