package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    ResponseEntity<CategoryDto> createCategory(NewCategoryDto categoryDto);

    void removeCategory(Long catId);

    ResponseEntity<CategoryDto> updateCategory(CategoryDto categoryDto, Long catId);

    ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size);

    ResponseEntity<CategoryDto> getCategoryById(Long catId);
}
