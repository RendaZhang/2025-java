package com.renda.taskmanager.service;

import com.renda.taskmanager.dto.CategoryDto;
import com.renda.taskmanager.entity.Category;
import com.renda.taskmanager.mapper.CategoryMapper;
import com.renda.taskmanager.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /* ---------- Read ---------- */

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryDto findOne(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        return categoryMapper.toDto(category);
    }

    /* ---------- Write ---------- */

    public CategoryDto create(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name " + name + " already exists");
        }
        return categoryMapper.toDto(categoryRepository.save(new Category(null, name, null)));
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

}
