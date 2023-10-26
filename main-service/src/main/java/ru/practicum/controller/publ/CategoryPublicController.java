package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {
    @Autowired
    private final CategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на получение категорий, from = {}, size = {}.", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        log.info("Принят public запрос на получение категории с id = {}.", catId);
        return service.getCategoryById(catId);
    }
}
