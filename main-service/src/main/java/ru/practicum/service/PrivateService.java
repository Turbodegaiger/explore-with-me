package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateService {
    ResponseEntity<List<EventShortDto>> getUserEvents(Long userId, Integer from, Integer size);

    ResponseEntity<EventFullDto> createEvent(Long userId, NewEventDto eventDto);

    ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId);

    ResponseEntity<EventFullDto> updateUserEvent(Long userId, Long eventId, UpdateEventRequest update);

    ResponseEntity<List<ParticipationRequestDto>> getRequestsToEvent(Long userId, Long eventId);

    ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(Long userId,
                                                                       Long eventId,
                                                                       EventRequestStatusUpdateRequest update);

    ResponseEntity<List<ParticipationRequestDto>> getUserRequests(Long userId);

    ResponseEntity<ParticipationRequestDto> createRequestToEvent(Long userId, Long eventId);

    ResponseEntity<ParticipationRequestDto> cancelRequestToEvent(Long userId, Long eventId);
}
