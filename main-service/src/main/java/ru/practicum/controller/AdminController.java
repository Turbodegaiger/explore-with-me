package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventsAdminSearchDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.service.AdminService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Controller
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {
    @Autowired
    private final AdminService service;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Принят admin запрос на создание категории: {}", categoryDto);
        return service.createCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> removeCategory(@PathVariable @NotEmpty Integer catId) {
        log.info("Принят admin запрос на удаление категории {}", catId);
        return service.removeCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                                 @PathVariable @NotEmpty Integer catId) {
        log.info("Принят admin запрос на обновление категории id {} на category {}", catId, categoryDto);
        return service.updateCategory(categoryDto, catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getEvents(@RequestParam(required = false) List<Integer> users,
                                            @RequestParam(required = false) List<String> states,
                                            @RequestParam(required = false) List<Integer> categories,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventsAdminSearchDto searchDto = new EventsAdminSearchDto(
                users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Принят admin запрос на поиск событий с параметрами: {}", searchDto);
        return service.getEvents(searchDto);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateEvent(@RequestBody @Valid UpdateEventAdminRequest update,
                                              @PathVariable Integer eventId) {
        log.info("Принят admin запрос на обновление события id {}, update = {}", eventId, update);
        return service.updateEvent(update, eventId);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUsers(@RequestParam(required = false) List<Integer> ids,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят admin запрос на получение списка пользователей с id {}, from = {}, size = {}", ids, from, size);
        return service.getUsers(ids, from, size);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Принят admin запрос на создание пользователя: {}", newUserRequest);
        return service.createUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteUser(@PathVariable Integer userId) {
        log.info("Принят admin запрос на удаление пользователя: {}", userId);
        return service.deleteUser(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Принят admin запрос на создание подборки: {}", newCompilationDto);
        return service.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteCompilation(@PathVariable Integer compId) {
        log.info("Принят admin запрос на удаление подборки: {}", compId);
        return service.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> updateCompilation(@RequestBody @Valid UpdateCompilationRequest update,
                                                    @PathVariable Integer compId) {
        log.info("Принят admin запрос на обновление подборки: {}, update = {}", compId, update);
        return service.updateCompilation(update, compId);
    }
}
