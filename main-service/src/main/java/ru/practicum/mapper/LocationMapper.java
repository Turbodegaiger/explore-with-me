package ru.practicum.mapper;

import ru.practicum.dto.location.Location;
import ru.practicum.model.LocationEntity;

public class LocationMapper {
    public static Location mapToLocation(LocationEntity location) {
        return new Location(location.getLat(), location.getLon());
    }
}
