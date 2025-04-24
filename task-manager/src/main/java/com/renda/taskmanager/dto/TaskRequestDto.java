package com.renda.taskmanager.dto;

import com.renda.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    @NotBlank private String title;
    private String description;
    @NotNull private TaskStatus status;
    @NotNull private Long categoryId;
}
