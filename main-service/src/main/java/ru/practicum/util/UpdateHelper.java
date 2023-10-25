package ru.practicum.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.enums.event.EventAdminStateAction;
import ru.practicum.enums.event.EventState;
import ru.practicum.enums.event.EventUserStateAction;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Event;

import java.time.LocalDateTime;

@Slf4j
public class UpdateHelper {
    public static EventState getStateForAdminUpdate(UpdateEventAdminRequest update, Event oldEvent) {
        EventState newState = oldEvent.getState();
        if (update.getStateAction() != null
                && EventAdminStateAction.valueOf(update.getStateAction()).equals(EventAdminStateAction.PUBLISH_EVENT)) {
            if (!oldEvent.getState().equals(EventState.PENDING)) {
                log.info("Cобытие можно публиковать, только если оно в состоянии ожидания публикации. " +
                        "Текущий event state={}.", oldEvent.getState());
                throw new DataConflictException(String.format("Cannot publish the event because event state should be " +
                        " PENDING. Current state is: %s.", oldEvent.getState()));
            }
            if (oldEvent.getEventDate()
                    .isBefore(DateTimeUtils.getCurrentTime().plusHours(1))) {
                log.info("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. " +
                        "eventDate={}, current time={}.", oldEvent.getEventDate(), DateTimeUtils.getCurrentTime());
                throw new ValidationException("Cannot publish the event because publishing time should be " +
                        "at least in one hour from event date.");
            }
            newState = EventState.PUBLISHED;
        } else {
            if (update.getStateAction() != null
                    && EventAdminStateAction.valueOf(update.getStateAction()).equals(EventAdminStateAction.REJECT_EVENT)) {
                if (oldEvent.getState().equals(EventState.PUBLISHED)) {
                    log.info("Cобытие можно отклонить, только если оно ещё не опубликовано. " +
                            "Текущий event state={}.", oldEvent.getState());
                    throw new DataConflictException(String.format("Cannot reject the event because event state should be " +
                            "not PUBLISHED. Current state is: %s.", oldEvent.getState()));
                }
                newState = EventState.CANCELED;
            }
        }
        return newState;
    }

    public static EventState getStateForUserUpdate(UpdateEventUserRequest update, Event oldEvent, EventState newState) {
        if (update.getStateAction() == null) {
            return newState;
        }
        if (!oldEvent.getState().equals(EventState.PENDING) && !oldEvent.getState().equals(EventState.CANCELED)) {
            log.info("Cобытие можно изменить, только если оно в состоянии ожидания публикации или отменено. " +
                    "Текущий event state={}.", oldEvent.getState());
            throw new DataConflictException(String.format("Cannot update event because event state should be " +
                    " PENDING or CANCELED. Current state is: %s.", oldEvent.getState()));
        } else if (EventUserStateAction.valueOf(update.getStateAction()).equals(EventUserStateAction.CANCEL_REVIEW)) {
            newState = EventState.CANCELED;
        } else if (EventUserStateAction.valueOf(update.getStateAction()).equals(EventUserStateAction.SEND_TO_REVIEW)) {
            newState = EventState.PENDING;
        }
        return newState;
    }

    public static LocalDateTime getEventDateForUpdate(UpdateEventRequest update, Event oldEvent) {
        LocalDateTime newDateTime = oldEvent.getEventDate();
        if (update.getEventDate() != null) {
            if (oldEvent.getPublishedOn() != null
                    && oldEvent.getPublishedOn().isAfter(DateTimeUtils.formatToLocalDT(update.getEventDate()).minusHours(1))) {
                log.info("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. " +
                        "new eventDate={}, publishedOn={}.", update.getEventDate(), oldEvent.getPublishedOn());
                throw new ValidationException(
                        String.format("Cannot change the event date because publishing datetime should be " +
                                        "at least in one hour from event date. new eventDate=%s, publishedOn=%s",
                                update.getEventDate(), oldEvent.getPublishedOn()));
            } else {
                if (DateTimeUtils.formatToLocalDT(update.getEventDate())
                        .isAfter(DateTimeUtils.getCurrentTime().plusHours(2))) {
                    log.info("Дата начала события должна быть не ранее чем через два часа от текущего времени. " +
                            "new eventDate={}, current time={}.", update.getEventDate(), DateTimeUtils.getCurrentTime());
                    throw new ValidationException(
                            String.format("Cannot change the event date because event date should be " +
                                            "at least in two hours from current datetime. new eventDate=%s, current time=%s",
                                    update.getEventDate(), DateTimeUtils.getCurrentTime()));
                }
            }
            newDateTime = DateTimeUtils.formatToLocalDT(update.getEventDate());
        }
        return newDateTime;
    }
}
