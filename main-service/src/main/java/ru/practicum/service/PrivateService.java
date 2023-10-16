package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;

public interface PrivateService {
    ResponseEntity<Object> getUserEvents(Integer userId, Integer from, Integer size);

    ResponseEntity<Object> createEvent(Integer userId, NewEventDto eventDto);

    ResponseEntity<Object> getUserEvent(Integer userId, Integer eventId);

    ResponseEntity<Object> updateUserEvent(Integer userId, Integer eventId, UpdateEventUserRequest update);

    ResponseEntity<Object> getRequestsToEvent(Integer userId, Integer eventId);

    ResponseEntity<Object> updateRequestStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest update);

    ResponseEntity<Object> getUserRequests(Integer userId);

    ResponseEntity<Object> createRequestToEvent(Integer userId, Integer eventId);

    ResponseEntity<Object> cancelRequestToEvent(Integer userId, Integer eventId);
}
