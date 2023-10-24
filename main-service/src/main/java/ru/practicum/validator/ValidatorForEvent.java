package ru.practicum.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.util.DateTimeUtils;

import java.time.format.DateTimeParseException;

@Slf4j
public class ValidatorForEvent {

    public static void validateDateOfNewEvent(NewEventDto event) {
        try {
            if (DateTimeUtils.formatToLocalDT(event.getEventDate())
                    .isBefore(DateTimeUtils.getCurrentTime().plusHours(2))) {
                log.info("{} не прошёл валидацию по полю eventDate: {}.", event.getClass(), event.getEventDate());
                throw new ValidationException(
                        "Field: eventDate. Error: must be at least 2 hours after current time. Value: " + event.getEventDate());
            }
        } catch (DateTimeParseException exception) {
            log.info("{} не прошёл валидацию по полю eventDate: {}.", event.getClass(), event.getEventDate());
            throw new ValidationException(
                    "Field: eventDate. Error: must be (yyyy-MM-dd HH:mm:ss) format. Value: " + event.getEventDate());
        }
    }
}
