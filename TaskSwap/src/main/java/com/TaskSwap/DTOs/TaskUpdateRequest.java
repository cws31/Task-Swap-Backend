package com.TaskSwap.DTOs;

import com.TaskSwap.enums.RewardType;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class TaskUpdateRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    private String category;
    private RewardType rewardType;
    private Double rewardAmount;
    private String preferredSwapSkill;
}
