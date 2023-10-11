package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;
import ru.practicum.util.DateTimeUtils;
import ru.practicum.validator.Validator;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {
    @Autowired
    private final StatsRepository repository;

    @Override
    public void saveHit(EndpointHit hit) {
        if (hit.getTimestamp().isEmpty()) {
            hit.setTimestamp(DateTimeUtils.formatToString(LocalDateTime.now()));
        }
        Validator.validateEndpointHit(hit);
        Stats stats = StatsMapper.toStats(hit);
        repository.save(stats);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        String encodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String encodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime startDateTime = DateTimeUtils.formatToLocalDT(encodedStart);
        LocalDateTime endDateTime = DateTimeUtils.formatToLocalDT(encodedEnd);
        List<ViewStats> statsList;
        if (uris.isEmpty()) {
            if (unique) {
                statsList = repository.findAllUriDistinct(startDateTime, endDateTime);
            } else {
                statsList = repository.findAllUri(startDateTime, endDateTime);
            }
        } else {
            if (unique) {
                statsList = repository.findAllUriIsInDistinct(uris, startDateTime, endDateTime);
            } else {
                statsList = repository.findAllUriIsIn(uris, startDateTime, endDateTime);
            }
        }
        if (statsList.isEmpty()) {
            log.info("В базе не найдено данных по запросу с параметрами: start={}, end={}, uris={}, unique={}",
                    start, end, uris, unique);
            throw new NotFoundException(
                    String.format("Не найдено записей по запросу с параметрами: start=%s, end=%s, uris=%s, unique=%s",
                            start, end, uris, unique));
        }
        return statsList;
    }
}
