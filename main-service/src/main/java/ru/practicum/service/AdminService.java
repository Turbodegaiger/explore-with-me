package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventsAdminSearchDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.NewUserRequest;

import java.util.List;
public interface AdminService {
    ResponseEntity<Object> createCategory(CategoryDto categoryDto);

    ResponseEntity<Object> removeCategory(Integer catId);

    ResponseEntity<Object> updateCategory(CategoryDto categoryDto, Integer catId);

    ResponseEntity<Object> getEvents(EventsAdminSearchDto searchDto);

    ResponseEntity<Object> updateEvent(UpdateEventAdminRequest update, Integer eventId);

    ResponseEntity<Object> getUsers(List<Integer> ids, Integer from, Integer size);

    ResponseEntity<Object> createUser(NewUserRequest newUserRequest);

    ResponseEntity<Object> deleteUser(Integer userId);

    ResponseEntity<Object> createCompilation(NewCompilationDto newCompilationDto);

    ResponseEntity<Object> deleteCompilation(Integer compId);

    ResponseEntity<Object> updateCompilation(UpdateCompilationRequest update, Integer compId);
}
