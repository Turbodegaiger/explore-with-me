package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.PublicService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final CompilationRepository compRepository;

    @Override
    public ResponseEntity<Object> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCompilationById(Long compId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCategories(Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getCategoryById(Long catId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getEvents(EventsPublicSearchDto searchDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getEventById(Long id) {
        return null;
    }
}
