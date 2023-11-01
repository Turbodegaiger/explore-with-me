package ru.practicum.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.event.EventSort;

import java.util.List;

@Data
@NoArgsConstructor
public class EventsPublicSearchDto {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
    private Integer from;
    private Integer size;

    public EventsPublicSearchDto(String text,
                                 List<Long> categories,
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
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        setSort(sort);
        this.from = from;
        this.size = size;
    }

    public void setSort(String sort) {
        if (sort != null && !sort.isEmpty()) {
            this.sort = EventSort.valueOf(sort);
        } else {
            this.sort = null;
        }
    }
}
