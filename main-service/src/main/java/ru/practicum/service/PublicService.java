package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.event.EventsPublicSearchDto;

public interface PublicService {
    ResponseEntity<Object> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<Object> getCompilationById(Integer compId);

    ResponseEntity<Object> getCategories(Integer from, Integer size);

    ResponseEntity<Object> getCategoryById(Integer catId);

    ResponseEntity<Object> getEvents(EventsPublicSearchDto searchDto);

    ResponseEntity<Object> getEventById(Integer id);
}
