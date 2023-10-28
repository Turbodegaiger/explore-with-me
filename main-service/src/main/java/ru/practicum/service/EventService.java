package ru.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.event.*;
import ru.practicum.dto.like.EventLikeStatisticsDto;
import ru.practicum.dto.like.LikeDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    ResponseEntity<List<EventFullDto>> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    ResponseEntity<EventFullDto> updateEvent(UpdateEventRequest update, Long eventId);

    ResponseEntity<List<EventShortDto>> getUserEvents(Long userId, Integer from, Integer size);

    ResponseEntity<EventFullDto> createEvent(Long userId, NewEventDto eventDto);

    ResponseEntity<EventFullDto> getUserEvent(Long userId, Long eventId);

    ResponseEntity<EventFullDto> updateUserEvent(Long userId, Long eventId, UpdateEventRequest update);

    ResponseEntity<List<EventShortDto>> getEvents(EventsPublicSearchDto searchDto, HttpServletRequest request);

    ResponseEntity<EventFullDto> getEventById(Long eventId, HttpServletRequest request);

    ResponseEntity<LikeDto> setLikeOrDislikeToEvent(Long userId, Long eventId, Boolean isLike);

    ResponseEntity<EventLikeStatisticsDto> getEventLikeStatistics(Long userId, Long eventId);

    void deleteLikeOrDislikeToEvent(Long userId, Long eventId);
}
