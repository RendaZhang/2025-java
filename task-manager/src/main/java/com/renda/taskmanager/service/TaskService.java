package com.renda.taskmanager.service;

import com.renda.taskmanager.entity.Task;
import com.renda.taskmanager.entity.TaskStatus;
import com.renda.taskmanager.exception.TaskNotFoundException;
import com.renda.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    /* ---------- Read Operations ---------- */

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Pagination + Sorting Query
     *
     * @param page Starts from 0
     * @param size Number of items per page
     * @param sortBy Sorting field
     */
    @Transactional(readOnly = true)
    public Page<Task> getTasks(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return taskRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasksByStatus(TaskStatus status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return taskRepository.findByStatus(status, pageable);
    }

    /* ---------- Write Operations ---------- */

    public Task createTask(Task task) {
        task.setCreatedTime(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

}
