package com.renda.taskmanager.service;

import com.renda.taskmanager.dto.TaskRequestDto;
import com.renda.taskmanager.dto.TaskResponseDto;
import com.renda.taskmanager.entity.Task;
import com.renda.taskmanager.entity.TaskStatus;
import com.renda.taskmanager.mapper.TaskMapper;
import com.renda.taskmanager.repository.CategoryRepository;
import com.renda.taskmanager.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final TaskMapper taskMapper;

    /* ---------- Read Operations ---------- */

    @Cacheable(value = "taskCache", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public TaskResponseDto findOne(Long id) throws EntityNotFoundException {
        return taskRepository.findById(id)
                .map(taskMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAll() {
        return taskMapper.toDtoList(taskRepository.findAll());
    }

    /**
     * Pagination + Sorting Query
     *
     * @param page Starts from 0
     * @param size Number of items per page
     * @param sortBy Sorting field
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> findPaged(int page, int size, String sortBy) {
        Pageable p = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return taskRepository.findAll(p).map(taskMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDto> findByStatus(TaskStatus status) {
        return taskMapper.toDtoList(taskRepository.findByStatus(status));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDto> findByStatusPaged(TaskStatus status, int page, int size, String sortBy) {
        Pageable p = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return taskRepository.findByStatus(status, p).map(taskMapper::toDto);
    }

    /* ---------- Write Operations ---------- */

    public TaskResponseDto create(TaskRequestDto req) {
        Task task = taskMapper.toEntity(req);
        task.setCategory(categoryRepository.getReferenceById(req.getCategoryId()));
        task.setCreatedTime(LocalDateTime.now());
        return taskMapper.toDto(taskRepository.save(task));
    }

    @CacheEvict(value = "taskCache", key = "#id")
    public TaskResponseDto update(Long id, TaskRequestDto req) throws EntityNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setStatus(req.getStatus());
        task.setCategory(categoryRepository.getReferenceById(req.getCategoryId()));
        return taskMapper.toDto(taskRepository.save(task));
    }

    @CacheEvict(value = "taskCache", key = "#id")
    public void delete(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        }
    }

}
