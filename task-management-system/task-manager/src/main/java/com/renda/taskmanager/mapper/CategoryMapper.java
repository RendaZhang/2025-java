package com.renda.taskmanager.mapper;

import com.renda.taskmanager.dto.CategoryDto;
import com.renda.taskmanager.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);
}
