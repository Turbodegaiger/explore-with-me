package ru.practicum.controller.privat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestPrivateController {
    @Autowired
    private final RequestService service;

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsToEvent(@PathVariable Long userId,
                                                                            @PathVariable Long eventId) {
        log.info("Принят private запрос на получение списка запросов на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.getRequestsToEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@PathVariable Long userId,
                                                                              @PathVariable Long eventId,
                                                                              @RequestBody EventRequestStatusUpdateRequest update) {
        log.info("Принят private запрос на обновление статуса запроса на участие в событии id = {} пользователем id = {}: {}.", eventId, userId, update);
        return service.updateRequestStatus(userId, eventId, update);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        log.info("Принят private запрос на получение списка запросов на участие в чужих событиях пользователем id = {}.", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ParticipationRequestDto> createRequestToEvent(@PathVariable Long userId,
                                                                        @RequestParam Long eventId) {
        log.info("Принят private запрос на создание запроса на участие в событии id = {} пользователем id = {}.", eventId, userId);
        return service.createRequestToEvent(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ParticipationRequestDto> cancelRequestToEvent(@PathVariable Long userId,
                                                                        @PathVariable Long requestId) {
        log.info("Принят private запрос на отмену запроса на участие id = {} пользователем id = {}.", requestId, userId);
        return service.cancelRequestToEvent(userId, requestId);
    }
}
