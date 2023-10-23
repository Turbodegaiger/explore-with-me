package ru.practicum.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.event.EventAdminState;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EventsAdminSearchDto {
    private List<Long> users;
    private List<EventAdminState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    public EventsAdminSearchDto(List<Long> users,
                                List<String> states,
                                List<Long> categories,
                                String rangeStart,
                                String rangeEnd,
                                Integer from,
                                Integer size) {
        this.users = users;
        setStates(states);
        this.categories = categories;
        setRangeStart(rangeStart);
        setRangeEnd(rangeEnd);
        this.from = from;
        this.size = size;
    }

    public void setRangeStart(String rangeStart) {
        this.rangeStart = DateTimeUtils.formatToLocalDT(rangeStart);
    }

    public void setRangeEnd(String rangeEnd) {
        this.rangeEnd = DateTimeUtils.formatToLocalDT(rangeEnd);
    }

    public void setStates(List<String> states) {
        List<EventAdminState> eventAdminStates = new ArrayList<>();
        if (states != null && !states.isEmpty()) {
            for (String s : states) {
                if (s != null) {
                    eventAdminStates.add(EventAdminState.valueOf(s));
                }
            }
        }
        this.states = eventAdminStates;
    }
}
