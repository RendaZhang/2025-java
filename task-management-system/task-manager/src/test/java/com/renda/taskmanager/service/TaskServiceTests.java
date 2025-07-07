package com.renda.taskmanager.service;

import com.renda.taskmanager.dto.CategoryDto;
import com.renda.taskmanager.dto.TaskRequestDto;
import com.renda.taskmanager.dto.TaskResponseDto;
import com.renda.taskmanager.entity.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@ActiveProfiles("test") // Use the configuration from application-test.yml
@Transactional
class TaskServiceTests {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testFindOne_CacheEnabled() {
        // 1️⃣ Prepare data
        CategoryDto categoryDto = categoryService.create("test_create");
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Test Task");
        request.setDescription("Testing Redis Cache");
        request.setStatus(TaskStatus.PENDING);
        request.setCategoryId(categoryDto.getId());
        TaskResponseDto savedTask = taskService.create(request);

        // 2️⃣ First call -> Read from database & cache
        TaskResponseDto dto1 = taskService.findOne(savedTask.getId());
        Assertions.assertNotNull(dto1);

        // 3️⃣ Read from cache
        TaskResponseDto cachedValue = cacheManager.getCache("taskCache").get(savedTask.getId(), TaskResponseDto.class);
        Assertions.assertNotNull(cachedValue);
        Assertions.assertEquals("Test Task", cachedValue.getTitle());

        // 4️⃣ Second call -> Read from cache, no database hit
        TaskResponseDto dto2 = taskService.findOne(savedTask.getId());
        Assertions.assertNotNull(dto2);
    }

    @Test
    void testUpdate_CacheEvict() {
        // 1️⃣ Create and cache
        CategoryDto categoryDto = categoryService.create("test_update");
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Old Title");
        request.setDescription("Old Description");
        request.setStatus(TaskStatus.DONE);
        request.setCategoryId(categoryDto.getId());
        TaskResponseDto savedTask = taskService.create(request);

        // 2️⃣ Trigger caching
        taskService.findOne(savedTask.getId());
        Assertions.assertNotNull(cacheManager.getCache("taskCache").get(savedTask.getId()));

        // 3️⃣ Update and check cache eviction
        request.setTitle("New Title");
        request.setDescription("New Description");
        request.setStatus(TaskStatus.PENDING);
        request.setCategoryId(categoryDto.getId());
        taskService.update(savedTask.getId(), request);
        Assertions.assertNull(cacheManager.getCache("taskCache").get(savedTask.getId()));
    }

    @Test
    void testDelete_CacheEvict() {
        // 1️⃣ Create and cache
        CategoryDto categoryDto = categoryService.create("test_detele");
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Delete Test");
        request.setDescription("Should be deleted");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setCategoryId(categoryDto.getId());
        TaskResponseDto savedTask = taskService.create(request);

        // 2️⃣ Trigger caching
        taskService.findOne(savedTask.getId());
        Assertions.assertNotNull(cacheManager.getCache("taskCache").get(savedTask.getId()));

        // 3️⃣ Delete and check cache eviction
        taskService.delete(savedTask.getId());
        Assertions.assertNull(cacheManager.getCache("taskCache").get(savedTask.getId()));
    }
}
