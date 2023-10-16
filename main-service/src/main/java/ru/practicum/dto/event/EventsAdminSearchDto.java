package ru.practicum.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.enums.event.EventAdminState;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class EventsAdminSearchDto {
    private List<Integer> users;
    private List<EventAdminState> states;
    private List<Integer> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Integer from;
    private Integer size;

    public EventsAdminSearchDto(List<Integer> users,
                                List<String> states,
                                List<Integer> categories,
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
