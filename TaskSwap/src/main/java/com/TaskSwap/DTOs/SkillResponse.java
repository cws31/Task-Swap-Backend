package com.TaskSwap.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SkillResponse {
    private Long id;
    private String title;
    private String description;
    private String postedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
