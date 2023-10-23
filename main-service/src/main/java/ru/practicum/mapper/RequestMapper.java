package ru.practicum.mapper;

import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;
import ru.practicum.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static ParticipationRequestDto mapRequestToRequestDto(Request request) {
        return new ParticipationRequestDto(
                DateTimeUtils.formatToString(request.getCreated()),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }

    public static List<ParticipationRequestDto> mapRequestToRequestDtoList(Iterable<Request> requestList) {
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        for (Request request : requestList) {
            requestDtoList.add(mapRequestToRequestDto(request));
        }
        return requestDtoList;
    }

    public static EventRequestStatusUpdateResult mapRequestToStatusUpdateDto(List<Request> confirmed, List<Request> rejected) {
        return new EventRequestStatusUpdateResult(
                mapRequestToRequestDtoList(confirmed),
                mapRequestToRequestDtoList(rejected));
    }
}
