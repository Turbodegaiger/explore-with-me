package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.AdminService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {
    @Autowired
    private final AdminService service;

    @PostMapping("/categories")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Принят admin запрос на создание категории: {}", newCategoryDto);
        return service.createCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info("Принят admin запрос на удаление категории {}", catId);
        service.removeCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                                      @PathVariable Long catId) {
        log.info("Принят admin запрос на обновление категории id {} на category {}", catId, categoryDto);
        return service.updateCategory(categoryDto, catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false, defaultValue = "") List<Long> users,
                                                  @RequestParam(required = false, defaultValue = "") List<String> states,
                                                  @RequestParam(required = false, defaultValue = "") List<Long> categories,
                                                  @RequestParam(required = false, defaultValue = "") String rangeStart,
                                                  @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят admin запрос на поиск событий с параметрами: " +
                "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}. ",
                users, states,categories, rangeStart, rangeEnd, from, size);
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @Valid UpdateEventRequest update,
                                                    @PathVariable Long eventId) {
        log.info("Принят admin запрос на обновление события id {}, update = {}", eventId, update);
        return service.updateEvent(update, eventId);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят admin запрос на получение списка пользователей с id {}, from = {}, size = {}", ids, from, size);
        return service.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Принят admin запрос на создание пользователя: {}", newUserRequest);
        return service.createUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Принят admin запрос на удаление пользователя: {}", userId);
        service.deleteUser(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Принят admin запрос на создание подборки: {}", newCompilationDto);
        return service.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Принят admin запрос на удаление подборки: {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<CompilationDto> updateCompilation(@RequestBody @Valid UpdateCompilationRequest update,
                                                            @PathVariable Long compId) {
        log.info("Принят admin запрос на обновление подборки: {}, update = {}", compId, update);
        return service.updateCompilation(update, compId);
    }
}
