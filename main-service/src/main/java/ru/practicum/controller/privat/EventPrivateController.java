package ru.practicum.controller.privat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    @Autowired
    private final EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getUserEvents(@PathVariable Long userId,
                                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят private запрос на поиск событий, добавленных пользователем id = {}: from = {}, size = {}.", userId, from, size);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDto eventDto) {
        log.info("Принят private запрос на создание события пользователем id = {}: {}.", userId, eventDto);
        return service.createEvent(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Принят private запрос на получение события id = {} пользователем id = {}.", eventId, userId);
        return service.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> updateUserEvent(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody @Valid UpdateEventRequest update) {
        log.info("Принят private запрос на обновление события id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateUserEvent(userId, eventId, update);
    }
}
