package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventsPublicSearchDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicService {
    ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<CompilationDto> getCompilationById(Long compId);

    ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size);

    ResponseEntity<CategoryDto> getCategoryById(Long catId);

    ResponseEntity<List<EventShortDto>> getEvents(EventsPublicSearchDto searchDto, HttpServletRequest request);

    ResponseEntity<EventFullDto> getEventById(Long eventId, HttpServletRequest request);
}
