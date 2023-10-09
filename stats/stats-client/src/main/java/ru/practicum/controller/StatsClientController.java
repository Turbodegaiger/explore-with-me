package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsClientController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveHit(@Valid @RequestBody EndpointHit hit) {
        log.info("Принят запрос на сохранение информации о запросе: {}" + "\n" +
                "Отправляем запрос на сервер stats-server...", hit);
        ResponseEntity<Object> result = statsClient.saveHit(hit);
        if (result.getStatusCode().is2xxSuccessful()) {
            log.info("Сохранение EndpointHit успешно выполнено: {}", hit);
        } else {
            log.info("Сохранение EndpointHit не удалось: {}", hit);
        }
        return result;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getStats(@RequestParam @NotEmpty String start,
                                           @RequestParam @NotEmpty String end,
                                           @RequestParam(required = false, defaultValue = "") List<String> uris,
                                           @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Принят запрос на получение статистики по посещениям: start={}, end={}, uris={}, unique={}" + "\n" +
                "Отправляем запрос на сервер stats-server...", start, end, uris, unique);
        ResponseEntity<Object> viewStatsList = statsClient.getStats(start, end, uris, unique);
        log.info("Принят ответ от сервера stats-server. " +"\n" +
                "Выгружен список статистики по посещениям: {}", viewStatsList);
        return viewStatsList;
    }
}
