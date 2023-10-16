package ru.practicum.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventsAdminSearchDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.service.AdminService;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Override
    public ResponseEntity<Object> createCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> removeCategory(Integer catId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> updateCategory(CategoryDto categoryDto, Integer catId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getEvents(EventsAdminSearchDto searchDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> updateEvent(UpdateEventAdminRequest update, Integer eventId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getUsers(List<Integer> ids, Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> createUser(NewUserRequest newUserRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Object> deleteUser(Integer userId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> createCompilation(NewCompilationDto newCompilationDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> deleteCompilation(Integer compId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> updateCompilation(UpdateCompilationRequest update, Integer compId) {
        return null;
    }
}
