package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.event.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private String title;
    private Long rating;
    private Boolean paid;
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator")
    private User initiator;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    private LocationEntity location;
    private Long views;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private Integer participantLimit;
    private Long confirmedRequests;
    private Boolean requestModeration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return getDescription().equals(event.getDescription()) &&
                getEventDate().equals(event.getEventDate()) &&
                getTitle().equals(event.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getEventDate(), getTitle());
    }
}
