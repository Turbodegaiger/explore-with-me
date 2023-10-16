package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.service.PrivateService;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateController {
    @Autowired
    private final PrivateService service;

    @GetMapping("/{userid}/events")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserEvents(@PathVariable Integer userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят private запрос на поиск событий, добавленных пользователем id = {}: from = {}, size = {}.", userId, from, size);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping("/{userid}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createEvent(@PathVariable Integer userId,
                                              @RequestBody @Valid NewEventDto eventDto) {
        log.info("Принят private запрос на создание события пользователем id = {}: {}.", userId, eventDto);
        return service.createEvent(userId, eventDto);
    }

    @GetMapping("/{userid}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserEvent(@PathVariable Integer userId,
                                               @PathVariable Integer eventId) {
        log.info("Принят private запрос на получение события id = {} пользователем id = {}.", eventId, userId);
        return service.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{userid}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUserEvent(@PathVariable Integer userId,
                                                  @PathVariable Integer eventId,
                                                  @RequestBody UpdateEventUserRequest update) {
        log.info("Принят private запрос на обновление события id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateUserEvent(userId, eventId, update);
    }

    @GetMapping("/{userid}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestsToEvent(@PathVariable Integer userId,
                                                     @PathVariable Integer eventId) {
        log.info("Принят private запрос на получение списка запросов на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.getRequestsToEvent(userId, eventId);
    }

    @PatchMapping("/{userid}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateRequestStatus(@PathVariable Integer userId,
                                                      @PathVariable Integer eventId,
                                                      @RequestBody EventRequestStatusUpdateRequest update) {
        log.info("Принят private запрос на обновление статуса запроса на участие в событии id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateRequestStatus(userId, eventId, update);
    }

    @GetMapping("/{userid}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserRequests(@PathVariable Integer userId) {
        log.info("Принят private запрос на получение списка запросов на участие в чужих событиях пользователем id = {}.", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping("/{userid}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequestToEvent(@PathVariable Integer userId,
                                                       @PathVariable Integer eventId) {
        log.info("Принят private запрос на создание запроса на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.createRequestToEvent(userId, eventId);
    }

    @PatchMapping("/{userid}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> cancelRequestToEvent(@PathVariable Integer userId,
                                                       @PathVariable Integer eventId) {
        log.info("Принят private запрос на отмену запроса на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.cancelRequestToEvent(userId, eventId);
    }
}
