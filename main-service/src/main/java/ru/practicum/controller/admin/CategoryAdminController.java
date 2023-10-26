package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryAdminController {
    @Autowired
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Принят admin запрос на создание категории: {}", newCategoryDto);
        return service.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info("Принят admin запрос на удаление категории {}", catId);
        service.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                                      @PathVariable Long catId) {
        log.info("Принят admin запрос на обновление категории id {} на category {}", catId, categoryDto);
        return service.updateCategory(categoryDto, catId);
    }
}
