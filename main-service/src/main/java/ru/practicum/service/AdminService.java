package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;
public interface AdminService {
    ResponseEntity<CategoryDto> createCategory(NewCategoryDto categoryDto);

    void removeCategory(Long catId);

    ResponseEntity<CategoryDto> updateCategory(CategoryDto categoryDto, Long catId);

    ResponseEntity<List<EventFullDto>> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    ResponseEntity<EventFullDto> updateEvent(UpdateEventAdminRequest update, Long eventId);

    ResponseEntity<List<UserDto>> getUsers(List<Long> ids, Integer from, Integer size);

    ResponseEntity<UserDto> createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    ResponseEntity<CompilationDto> createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    ResponseEntity<CompilationDto> updateCompilation(UpdateCompilationRequest update, Long compId);
}
