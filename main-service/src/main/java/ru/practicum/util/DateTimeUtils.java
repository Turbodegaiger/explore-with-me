package ru.practicum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatToString(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            return formatter.format(localDateTime);
        } else {
            return null;
        }
    }

    public static LocalDateTime formatToLocalDT(String dateTime) {
        if (dateTime != null) {
            return LocalDateTime.parse(dateTime, formatter);
        } else {
            return null;
        }
    }
}
