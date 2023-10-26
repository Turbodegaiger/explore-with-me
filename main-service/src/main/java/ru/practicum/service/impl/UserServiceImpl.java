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
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageParams = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        List<UserDto> userDtoList;
        if (ids.isEmpty()) {
            userDtoList = UserMapper.mapUsersToUserDtoList(userRepository.findAll(pageParams));
        } else {
            userDtoList = UserMapper.mapUsersToUserDtoList(userRepository.findAllByIdIn(ids, pageParams));
        }
        log.info("Успешно выгружен список пользователей по id: {}. Результат: {}", ids, userDtoList);
        return ResponseEntity.of(Optional.of(userDtoList));
    }

    @Override
    public ResponseEntity<UserDto> createUser(NewUserRequest newUserRequest) {
        User newUser = userRepository.save(UserMapper.mapNewUserRequestToUser(newUserRequest));
        UserDto userDto = UserMapper.mapUserToUserDto(newUser);
        log.info("Успешно создан новый пользователь: {}", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id=%s is not found, check request.", userId)));
        userRepository.deleteById(userId);
        log.info("Успешно удалён пользователь c id={}.", userId);
    }
}
