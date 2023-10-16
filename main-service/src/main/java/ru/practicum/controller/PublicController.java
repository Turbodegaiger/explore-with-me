package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.service.PublicService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {
    @Autowired
    private final PublicService service;

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на поиск подборок событий: pinned = {}, from = {}, size = {}", pinned, from, size);
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCompilationById(@PathVariable Integer compId) {
        log.info("Принят public запрос на поиск подборки событий с id = {}", compId);
        return service.getCompilationById(compId);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят public запрос на получение категорий, from = {}, size = {}.", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCategoryById(@PathVariable Integer catId) {
        log.info("Принят public запрос на получение категории с id = {}.", catId);
        return service.getCategoryById(catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Integer> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(required = false) String sort,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventsPublicSearchDto searchDto = new EventsPublicSearchDto(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Принят public запрос на получение событий, from = {}, size = {}.", from, size);
        return service.getEvents(searchDto);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEventById(@PathVariable Integer id) {
        log.info("Принят public запрос на получение события с id = {}.", id);
        return service.getEventById(id);
    }
}
