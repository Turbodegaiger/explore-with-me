package ru.practicum.mapper;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User mapNewUserRequestToUser(NewUserRequest newUserRequest) {
        return new User(0L, newUserRequest.getName(), newUserRequest.getEmail());
    }

    public static UserDto mapUserToUserDto(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    public static List<UserDto> mapUsersToUserDtoList(Iterable<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            userDtoList.add(mapUserToUserDto(user));
        }
        return userDtoList;
    }

    public static UserShortDto mapUserToUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
