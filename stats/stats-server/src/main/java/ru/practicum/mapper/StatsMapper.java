package ru.practicum.mapper;

import ru.practicum.dto.EndpointHit;
import ru.practicum.model.Stats;
import ru.practicum.util.DateTimeUtils;

public class StatsMapper {
    public static EndpointHit toEndpointHit(Stats stats) {
        return new EndpointHit(
                stats.getId().intValue(),
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                DateTimeUtils.formatToString(stats.getTimestamp()));
    }

    public static Stats toStats(EndpointHit hit) {
        Long hitId;
        if (hit.getId() == null) {
            hitId = null;
        } else {
            hitId = hit.getId().longValue();
        }
        return new Stats(
                hitId,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                DateTimeUtils.formatToLocalDT(hit.getTimestamp()));
    }
}
