package ru.practicum.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.enums.event.EventSort;
import ru.practicum.util.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class EventsPublicSearchDto {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
    private Integer from;
    private Integer size;

    public EventsPublicSearchDto(String text,
                                 List<Integer> categories,
                                 Boolean paid,
                                 String rangeStart,
                                 String rangeEnd,
                                 Boolean onlyAvailable,
                                 String sort,
                                 Integer from,
                                 Integer size) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        setRangeStart(rangeStart);
        setRangeEnd(rangeEnd);
        this.onlyAvailable = onlyAvailable;
        setSort(sort);
        this.from = from;
        this.size = size;
    }

    public void setRangeStart(String rangeStart) {
        this.rangeStart = DateTimeUtils.formatToLocalDT(rangeStart);
    }

    public void setRangeEnd(String rangeEnd) {
        this.rangeEnd = DateTimeUtils.formatToLocalDT(rangeEnd);
    }

    public void setSort(String sort) {
        if (sort != null) {
            this.sort = EventSort.valueOf(sort);
        } else {
            this.sort = null;
        }
    }
}
