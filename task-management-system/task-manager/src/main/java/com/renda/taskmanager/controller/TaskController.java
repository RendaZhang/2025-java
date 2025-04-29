package com.renda.taskmanager.controller;

import com.renda.taskmanager.dto.TaskRequestDto;
import com.renda.taskmanager.dto.TaskResponseDto;
import com.renda.taskmanager.entity.TaskStatus;
import com.renda.taskmanager.exception.TaskNotFoundException;
import com.renda.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long id) throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size,
                                      @RequestParam(defaultValue = "createdTime") String sortBy) {
        if (page == null || size == null) {
            return ResponseEntity.ok(taskService.findAll());
        }
        return ResponseEntity.ok(taskService.findPaged(page, size, sortBy));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable("status") TaskStatus status,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size,
                                              @RequestParam(defaultValue = "createdTime") String sortBy) {
        if (page == null || size == null) {
            return ResponseEntity.ok(taskService.findByStatus(status));
        }
        return ResponseEntity.ok(taskService.findByStatusPaged(status, page, size, sortBy));
    }

    /* ---------- Write Endpoints ---------- */

    @PostMapping
    public ResponseEntity<TaskResponseDto> create(@Valid @RequestBody TaskRequestDto req) {
        TaskResponseDto created = taskService.create(req);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> update(@PathVariable Long id,
                                                  @RequestBody TaskRequestDto req)
            throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

}
