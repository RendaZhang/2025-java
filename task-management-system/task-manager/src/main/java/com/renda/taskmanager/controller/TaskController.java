package com.renda.taskmanager.controller;

import com.renda.common.dto.CommonResponseDto;
import com.renda.common.util.ResponseUtils;
import com.renda.taskmanager.dto.TaskRequestDto;
import com.renda.taskmanager.dto.TaskResponseDto;
import com.renda.taskmanager.entity.TaskStatus;
import com.renda.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Task Management", description = "Operations related to tasks")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /* ---------- Read Endpoints ---------- */

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<TaskResponseDto>> getTask(@PathVariable Long id) {
        return ResponseUtils.success(taskService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<Page<TaskResponseDto>>> getTasks(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdTime") String sortBy) {
        return ResponseUtils.success(taskService.findPaged(page, size, sortBy));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<CommonResponseDto<Page<TaskResponseDto>>> getTasksByStatus(
            @PathVariable("status") TaskStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdTime") String sortBy) {
        return ResponseUtils.success(taskService.findByStatusPaged(status, page, size, sortBy));
    }

    /* ---------- Write Endpoints ---------- */

    @PostMapping
    public ResponseEntity<CommonResponseDto<TaskResponseDto>> create(@Valid @RequestBody TaskRequestDto req) {
        TaskResponseDto created = taskService.create(req);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseUtils.created(location, created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDto<TaskResponseDto>> update(@PathVariable Long id,
                                                  @RequestBody TaskRequestDto req) {
        return ResponseUtils.success(taskService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto<String>> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseUtils.success("Task deleted successfully");
    }

}
