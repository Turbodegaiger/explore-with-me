package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size);

    ResponseEntity<UserDto> createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
