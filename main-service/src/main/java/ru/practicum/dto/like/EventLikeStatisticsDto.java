package ru.practicum.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventLikeStatisticsDto {
    EventShortDto event;
    Long rating;
    List<UserShortDto> usersLiked;
    List<UserShortDto> usersDisliked;
}
