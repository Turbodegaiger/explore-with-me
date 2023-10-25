package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.PrivateService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateController {
    @Autowired
    private final PrivateService service;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getUserEvents(@PathVariable Long userId,
                                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят private запрос на поиск событий, добавленных пользователем id = {}: from = {}, size = {}.", userId, from, size);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDto eventDto) {
        log.info("Принят private запрос на создание события пользователем id = {}: {}.", userId, eventDto);
        return service.createEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Принят private запрос на получение события id = {} пользователем id = {}.", eventId, userId);
        return service.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> updateUserEvent(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody @Valid UpdateEventRequest update) {
        log.info("Принят private запрос на обновление события id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateUserEvent(userId, eventId, update);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsToEvent(@PathVariable Long userId,
                                                                            @PathVariable Long eventId) {
        log.info("Принят private запрос на получение списка запросов на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.getRequestsToEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@PathVariable Long userId,
                                                                              @PathVariable Long eventId,
                                                                              @RequestBody EventRequestStatusUpdateRequest update) {
        log.info("Принят private запрос на обновление статуса запроса на участие в событии id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateRequestStatus(userId, eventId, update);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.info("Принят private запрос на получение списка запросов на участие в чужих событиях пользователем id = {}.", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ParticipationRequestDto> createRequestToEvent(@PathVariable Long userId,
                                                                        @RequestParam Long eventId) {
        log.info("Принят private запрос на создание запроса на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.createRequestToEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ParticipationRequestDto> cancelRequestToEvent(@PathVariable Long userId,
                                                                        @PathVariable Long requestId) {
        log.info("Принят private запрос на отмену запроса на участие в событии id = {} пользователем id = {}.", requestId, userId);
        return service.cancelRequestToEvent(userId, requestId);
    }
}
