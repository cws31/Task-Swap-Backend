package com.TaskSwap.DTOs;

import lombok.*;
import java.time.LocalDateTime;
import com.TaskSwap.enums.RewardType;
import com.TaskSwap.enums.TaskStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private RewardType rewardType;
    private String rewardAmount;
    private String preferredSwapSkill;
    private TaskStatus status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
