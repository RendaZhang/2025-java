package com.renda.taskmanager.controller;

import com.renda.common.util.ResponseUtils;
import com.renda.taskmanager.dto.CategoryDto;
import com.renda.common.dto.CommonResponseDto;
import com.renda.taskmanager.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Category Management", description = "Operations related to categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CommonResponseDto<List<CategoryDto>>> getAll() {
        return ResponseUtils.success(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<CategoryDto>> getById(@PathVariable Long id) {
        return ResponseUtils.success(categoryService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<CommonResponseDto<CategoryDto>> create(@RequestParam String name) {
        CategoryDto created = categoryService.create(name);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseUtils.created(location, created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto<String>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseUtils.success("Category with id " + id + " deleted.");
    }

}
