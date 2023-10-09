package ru.practicum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatToString(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }

    public static LocalDateTime formatToLocalDT(String dateTime) {
        return LocalDateTime.parse(dateTime, formatter);
    }
}
