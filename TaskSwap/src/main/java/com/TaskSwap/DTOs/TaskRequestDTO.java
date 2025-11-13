package com.TaskSwap.DTOs;

import com.TaskSwap.enums.RequestStatus;
import com.TaskSwap.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDTO {
    private Long id;
    private String requesterUsername;
    private String receiverUsername;
    private String requesterFullName;
    private String receiverFullName;
    private String taskTitle;
    private RequestType requestType;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
