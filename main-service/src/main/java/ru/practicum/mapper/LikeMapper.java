package ru.practicum.mapper;

import ru.practicum.dto.like.LikeDto;
import ru.practicum.model.Like;

public class LikeMapper {
    public static LikeDto mapLikeToLikeDto(Like like) {
        return new LikeDto(
                like.getEvent().getId(),
                like.getUser().getId(),
                like.getIsLiked());
    }
}
