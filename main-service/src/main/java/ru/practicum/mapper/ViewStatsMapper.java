package ru.practicum.mapper;

import ru.practicum.dto.ViewStats;

public class ViewStatsMapper {
    public static ViewStats mapStringToViewStats(String text) {
        String[] fields = text.split(", ");
        return new ViewStats(
                fields[0].substring(6),
                fields[1].substring(4),
                Integer.valueOf(fields[2].substring(5, fields[2].length() - 2))
        );
    }
}
