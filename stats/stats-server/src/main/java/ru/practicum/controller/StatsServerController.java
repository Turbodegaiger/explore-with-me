package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatsServerServiceImpl;

import java.util.List;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsServerController {
    @Autowired
    private final StatsServerServiceImpl service;

    @PostMapping(value = "/hit", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody EndpointHit hit) {
        log.info("Принят запрос на сохранение информации о запросе: {}", hit);
        service.saveHit(hit);
        log.info("Информация о запросе сохранена: {}", hit);
    }

    @GetMapping(value = "/stats", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Принят запрос на получение статистики по посещениям: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);
        List<ViewStats> viewStatsList = service.getStats(start, end, uris, unique);
        log.info("Выгружен список статистики по посещениям: {}", viewStatsList);
        return viewStatsList;
    }
}
