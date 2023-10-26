package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventsPublicSearchDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    @Autowired
    private final EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false, defaultValue = "") String text,
            @RequestParam(required = false, defaultValue = "") List<Long> categories,
            @RequestParam(required = false, defaultValue = "") Boolean paid,
            @RequestParam(required = false, defaultValue = "") String rangeStart,
            @RequestParam(required = false, defaultValue = "") String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        EventsPublicSearchDto searchDto = new EventsPublicSearchDto(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Принят public запрос на получение событий, from = {}, size = {}.", from, size);
        return service.getEvents(searchDto, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Принят public запрос на получение события с id = {}.", eventId);
        return service.getEventById(eventId, request);
    }
}
