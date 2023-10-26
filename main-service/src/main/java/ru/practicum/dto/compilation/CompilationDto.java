package ru.practicum.dto.compilation;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;

import java.util.List;

@Data
@NoArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;

    public CompilationDto(List<Event> events, Long id, Boolean pinned, String title) {
        setEventShortDtosFromEvents(events);
        this.id = id;
        this.pinned = pinned;
        this.title = title;
    }

    public void setEventShortDtosFromEvents(List<Event> events) {
        this.events = EventMapper.mapEventToEventShortDtoList(events);
    }
}
