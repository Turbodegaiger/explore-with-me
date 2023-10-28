package ru.practicum.mapper;

import ru.practicum.dto.event.*;
import ru.practicum.enums.event.EventState;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.LocationEntity;
import ru.practicum.model.User;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {
    public static Event mapNewEventDtoToEvent(NewEventDto newEventDto, Category category, User initiator, LocationEntity location) {
        return new Event(
                0L,
                newEventDto.getAnnotation(),
                category,
                newEventDto.getDescription(),
                DateTimeUtils.formatToLocalDT(newEventDto.getEventDate()),
                DateTimeUtils.getCurrentTime(),
                null,
                newEventDto.getTitle(),
                0L,
                newEventDto.getPaid(),
                true,
                initiator,
                location,
                0L,
                EventState.PENDING,
                newEventDto.getParticipantLimit(),
                0L,
                newEventDto.getRequestModeration());
    }

    public static EventFullDto mapEventToEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeUtils.formatToString(event.getCreatedOn()),
                event.getDescription(),
                DateTimeUtils.formatToString(event.getEventDate()),
                event.getId(),
                UserMapper.mapUserToUserShortDto(event.getInitiator()),
                LocationMapper.mapToLocation(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                DateTimeUtils.formatToString(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                event.getRating(),
                event.getViews());
    }

    public static List<EventFullDto> mapEventToEventFullDtoList(Iterable<Event> foundEvents) {
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : foundEvents) {
            eventFullDtoList.add(mapEventToEventFullDto(event));
        }
        return eventFullDtoList;
    }

    public static Event mapUpdateToEvent(UpdateEventRequest update,
                                         Event oldEvent,
                                         Long eventId,
                                         EventState newState,
                                         Category newCategory,
                                         LocalDateTime dateTime) {
        Event updatedEvent = new Event();
        updatedEvent.setId(eventId);
        if (update.getAnnotation() != null) {
            updatedEvent.setAnnotation(update.getAnnotation());
        } else {
            updatedEvent.setAnnotation(oldEvent.getAnnotation());
        }
        updatedEvent.setCategory(newCategory);
        if (update.getDescription() != null) {
            updatedEvent.setDescription(update.getDescription());
        } else {
            updatedEvent.setDescription(oldEvent.getDescription());
        }
        updatedEvent.setEventDate(dateTime);
        updatedEvent.setCreatedOn(oldEvent.getCreatedOn());
        if (newState.equals(EventState.PUBLISHED)) {
            updatedEvent.setPublishedOn(DateTimeUtils.getCurrentTime());
        }
        if (update.getTitle() != null) {
            updatedEvent.setTitle(update.getTitle());
        } else {
            updatedEvent.setTitle(oldEvent.getTitle());
        }
        updatedEvent.setRating(oldEvent.getRating());
        if (update.getPaid() != null) {
            updatedEvent.setPaid(update.getPaid());
        } else {
            updatedEvent.setPaid(oldEvent.getPaid());
        }
        updatedEvent.setAvailable(oldEvent.getAvailable());
        updatedEvent.setInitiator(oldEvent.getInitiator());
        updatedEvent.setViews(oldEvent.getViews());
        updatedEvent.setState(newState);
        if (update.getLocation() != null) {
            updatedEvent.setLocation(
                    new LocationEntity(
                    oldEvent.getLocation().getId(),
                    update.getLocation().getLat(),
                    update.getLocation().getLon()));
        } else {
            updatedEvent.setLocation(oldEvent.getLocation());
        }
        if (update.getParticipantLimit() != null) {
            updatedEvent.setParticipantLimit(update.getParticipantLimit());
        } else {
            updatedEvent.setParticipantLimit(oldEvent.getParticipantLimit());
        }
        updatedEvent.setConfirmedRequests(oldEvent.getConfirmedRequests());
        if (update.getRequestModeration() != null) {
            updatedEvent.setRequestModeration(update.getRequestModeration());
        } else {
            updatedEvent.setRequestModeration(oldEvent.getRequestModeration());
        }
        return updatedEvent;
    }

    public static List<EventShortDto> mapEventToEventShortDtoList(Iterable<Event> foundEvents) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : foundEvents) {
            eventShortDtoList.add(mapEventToEventShortDto(event));
        }
        return eventShortDtoList;
    }

    public static EventShortDto mapEventToEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeUtils.formatToString(event.getEventDate()),
                event.getId(),
                UserMapper.mapUserToUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getRating(),
                event.getViews());
    }
}
