package com.renda.taskmanager.dto;

import com.renda.taskmanager.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdTime;
}
