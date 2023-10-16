package ru.practicum.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.service.PublicService;

@Service
public class PublicServiceImpl implements PublicService {
    @Override
    public ResponseEntity<Object> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCompilationById(Integer compId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCategories(Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCategoryById(Integer catId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getEvents(EventsPublicSearchDto searchDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getEventById(Integer id) {
        return null;
    }
}
