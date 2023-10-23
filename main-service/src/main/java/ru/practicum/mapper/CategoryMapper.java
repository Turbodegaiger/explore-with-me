package ru.practicum.mapper;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

public class CategoryMapper {
    public static Category mapNewCategoryDtoToCategory(NewCategoryDto newCategoryDto) {
        return new Category(0L, newCategoryDto.getName());
    }

    public static CategoryDto mapCategoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category mapCategoryDtoToCategory(CategoryDto category, Long catId) {
        return new Category(catId, category.getName());
    }
}
