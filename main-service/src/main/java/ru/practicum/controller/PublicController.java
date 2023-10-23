package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.service.PublicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    @Autowired
    private final PublicService service;

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на поиск подборок событий: pinned = {}, from = {}, size = {}", pinned, from, size);
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        log.info("Принят public запрос на поиск подборки событий с id = {}", compId);
        return service.getCompilationById(compId);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на получение категорий, from = {}, size = {}.", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        log.info("Принят public запрос на получение категории с id = {}.", catId);
        return service.getCategoryById(catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestParam(required = false, defaultValue = "") List<Long> categories,
            @RequestParam(required = false, defaultValue = "") Boolean paid,
            @RequestParam(required = false, defaultValue = "") String rangeStart,
            @RequestParam(required = false, defaultValue = "") String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventsPublicSearchDto searchDto = new EventsPublicSearchDto(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Принят public запрос на получение событий, from = {}, size = {}.", from, size);
        return service.getEvents(searchDto);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEventById(@PathVariable Long id) {
        log.info("Принят public запрос на получение события с id = {}.", id);
        return service.getEventById(id);
    }
}
