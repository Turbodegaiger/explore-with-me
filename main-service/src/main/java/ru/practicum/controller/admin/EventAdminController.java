package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    @Autowired
    private final EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventFullDto>> getEvents(@RequestParam(required = false, defaultValue = "") List<Long> users,
                                                        @RequestParam(required = false, defaultValue = "") List<String> states,
                                                        @RequestParam(required = false, defaultValue = "") List<Long> categories,
                                                        @RequestParam(required = false, defaultValue = "") String rangeStart,
                                                        @RequestParam(required = false, defaultValue = "") String rangeEnd,
                                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Принят admin запрос на поиск событий с параметрами: " +
                        "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}. ",
                users, states,categories, rangeStart, rangeEnd, from, size);
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @Valid UpdateEventRequest update,
                                                    @PathVariable Long eventId) {
        log.info("Принят admin запрос на обновление события id {}, update = {}", eventId, update);
        return service.updateEvent(update, eventId);
    }
}
