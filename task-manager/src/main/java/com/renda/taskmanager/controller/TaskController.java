package com.renda.taskmanager.controller;

import com.renda.taskmanager.entity.Task;
import com.renda.taskmanager.entity.TaskStatus;
import com.renda.taskmanager.exception.TaskNotFoundException;
import com.renda.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /* ---------- Read Endpoints ---------- */

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
        return taskService.getTaskById(id).map(ResponseEntity::ok).orElseThrow(() -> new TaskNotFoundException(id));
    }

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size,
                                      @RequestParam(defaultValue = "createdTime") String sortBy) {
        if (page == null || size == null) {
            return ResponseEntity.ok(taskService.getAllTasks());
        }
        return ResponseEntity.ok(taskService.getTasks(page, size, sortBy));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTasksByStatus(@PathVariable("status") TaskStatus status,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size,
                                              @RequestParam(defaultValue = "createdTime") String sortBy) {
        if (page == null || size == null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status));
        }
        return ResponseEntity.ok(taskService.getTasksByStatus(status, page, size, sortBy));
    }

    /* ---------- Write Endpoints ---------- */

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) throws TaskNotFoundException {
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }
}
