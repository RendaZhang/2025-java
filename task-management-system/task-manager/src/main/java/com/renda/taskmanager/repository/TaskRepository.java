package com.renda.taskmanager.repository;

import com.renda.taskmanager.entity.Task;
import com.renda.taskmanager.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByStatusAndTitleContainingIgnoreCase(TaskStatus status, String keyword);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    List<Task> findByCreatedTimeBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.createdTime < :before")
    List<Task> findOldTasks(@Param("status") TaskStatus status, @Param("before") LocalDateTime before);

    @Query(value = "SELECT * FROM tasks ORDER BY LENGTH(description) DESC LIMIT :limit", nativeQuery = true)
    List<Task> findTopByLongestDescription(@Param("limit") int limit);

}
