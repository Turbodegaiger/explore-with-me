package ru.practicum.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.service.PrivateService;

@Service
public class PrivateServiceImpl implements PrivateService {
    @Override
    public ResponseEntity<Object> getUserEvents(Integer userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<Object> createEvent(Integer userId, NewEventDto eventDto) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getUserEvent(Integer userId, Integer eventId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> updateUserEvent(Integer userId, Integer eventId, UpdateEventUserRequest update) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getRequestsToEvent(Integer userId, Integer eventId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> updateRequestStatus(Integer userId, Integer eventId, EventRequestStatusUpdateRequest update) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getUserRequests(Integer userId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> createRequestToEvent(Integer userId, Integer eventId) {
        return null;
    }

    @Override
    public ResponseEntity<Object> cancelRequestToEvent(Integer userId, Integer eventId) {
        return null;
    }
}
