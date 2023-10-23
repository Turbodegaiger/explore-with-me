package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.event.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
//@NamedNativeQuery(name = "GetEventFullDto",
//        query = "select e.id as id, " +
//                "e.annotation as annotation, " +
//                "e.category as category, " +
//                "e.confirmed_requests as confirmed_requests, " +
//                "e.created_on as created_on, " +
//                "e.description as description, " +
//                "e.event_date as event_date, " +
//                "e.initiator as initiator, " +
//                "e.paid as paid, " +
//                "e.participant_limit as participant_limit, " +
//                "e.published_on as published_on, " +
//                "e.request_moderation as request_moderation, " +
//                "e.state as state, " +
//                "e.title as title, " +
//                "e.views as views " +
//                "from events as e where e.id=?1",
//        resultSetMapping = "EventFullDtoResult")
//@SqlResultSetMapping(name = "EventFullDtoResult",
//        entities = {
//                @EntityResult(entityClass = ru.practicum.model.Event.class, fields = {
//                        @FieldResult(name = "annotation", column = "annotation"),
//                        @FieldResult(name = "category", column = "category"),
//                        @FieldResult(name = "confirmedRequests", column = "confirmed_requests"),
//                        @FieldResult(name = "createdOn", column = "created_on"),
//                        @FieldResult(name = "description", column = "description"),
//                        @FieldResult(name = "eventDate", column = "event_date"),
//                        @FieldResult(name = "id", column = "id"),
//                        @FieldResult(name = "initiator", column = "initiator"),
//                        @FieldResult(name = "paid", column = "paid"),
//                        @FieldResult(name = "participantLimit", column = "participant_limit"),
//                        @FieldResult(name = "publishedOn", column = "published_on"),
//                        @FieldResult(name = "requestModeration", column = "request_moderation"),
//                        @FieldResult(name = "state", column = "state"),
//                        @FieldResult(name = "title", column = "title"),
//                        @FieldResult(name = "views", column = "views"),
//                })}
//)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator")
    private User initiator;
    private Long views;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private Integer participantLimit;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_requests")
    private List<Request> confirmedRequests;
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
