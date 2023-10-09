package ru.practicum.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface StatsServerService {
    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique);
    void saveHit(EndpointHit hit);
}
