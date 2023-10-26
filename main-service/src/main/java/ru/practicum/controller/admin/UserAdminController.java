package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {
    @Autowired
    private final UserService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false, defaultValue = "") List<Long> ids,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят admin запрос на получение списка пользователей с id {}, from = {}, size = {}", ids, from, size);
        return service.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Принят admin запрос на создание пользователя: {}", newUserRequest);
        return service.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Принят admin запрос на удаление пользователя: {}", userId);
        service.deleteUser(userId);
    }
}
