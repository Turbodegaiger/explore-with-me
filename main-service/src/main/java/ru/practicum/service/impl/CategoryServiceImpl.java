package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ResponseEntity<CategoryDto> createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.mapNewCategoryDtoToCategory(newCategoryDto));
        CategoryDto categoryDto = CategoryMapper.mapCategoryToCategoryDto(category);
        log.info("Успешно создана новая категория: {}", categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @Override
    public void removeCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.", catId)));
        Optional<Event> event = eventRepository.findByCategoryIdEquals(catId);
        if (event.isEmpty()) {
            categoryRepository.deleteById(catId);
            log.info("Успешно удалена категория: {}", catId);
        } else {
            log.info("Не удалось удалить категорию с id={}: на данную категорию ссылаются события.", catId);
            throw new DataConflictException("The category is not empty.");
        }
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(CategoryDto update, Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.", catId)));
        Category updatedCategory = categoryRepository.save(CategoryMapper.mapCategoryDtoToCategory(update, catId));
        CategoryDto updatedCategoryDto = CategoryMapper.mapCategoryToCategoryDto(updatedCategory);
        log.info("Успешно обновлена категория: {}", updatedCategoryDto);
        return ResponseEntity.of(Optional.of(updatedCategoryDto));
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(
                from > 0 ? from / size : 0, size, Sort.by(Sort.DEFAULT_DIRECTION, "name"));
        List<CategoryDto> categoryDtoList = CategoryMapper.mapCategoryToCategoryDtoList(
                categoryRepository.findAll(pageParams));
        log.info("Успешно выгружены категории по параметрам " +
                "from={}, size={}. Value: {}", from, size, categoryDtoList);
        return ResponseEntity.of(Optional.of(categoryDtoList));
    }

    @Override
    public ResponseEntity<CategoryDto> getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found.", catId)));
        CategoryDto categoryDto = CategoryMapper.mapCategoryToCategoryDto(category);
        log.info("Успешно выгружена категория по id={}. Value: {}.", catId, categoryDto);
        return ResponseEntity.of(Optional.of(categoryDto));
    }
}
