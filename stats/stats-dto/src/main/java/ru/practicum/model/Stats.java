package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@AllArgsConstructor
@NoArgsConstructor
@NamedNativeQuery(name = "ViewStatsAllUriDistinct",
        query = "select h.app as app, " +
                "h.uri as uri, " +
                "count(distinct h.ip) as hits " +
                "from hits as h where h.timestamp between ?1 and ?2 group by h.uri order by hits desc",
        resultSetMapping = "ViewStatsResult")
@NamedNativeQuery(name = "ViewStatsAllUri",
        query = "select h.app as app, " +
                "h.uri as uri, " +
                "count(h.uri) as hits " +
                "from hits as h where h.timestamp between ?1 and ?2 group by h.app, h.uri order by hits desc",
        resultSetMapping = "ViewStatsResult")
@NamedNativeQuery(name = "ViewStatsDistinct",
        query = "select h.app as app, " +
                "h.uri as uri, " +
                "count(distinct h.ip) as hits " +
                "from hits as h where h.uri in (?1) and (h.timestamp between ?2 and ?3) group by h.uri order by hits desc",
        resultSetMapping = "ViewStatsResult")
@NamedNativeQuery(name = "ViewStats",
        query = "select h.app as app, " +
                "h.uri as uri, " +
                "count(h.uri) as hits " +
                "from hits as h where h.uri in (?1) and (h.timestamp between ?2 and ?3) group by h.app, h.uri order by hits desc",
        resultSetMapping = "ViewStatsResult")
@SqlResultSetMapping(name = "ViewStatsResult",
        entities = {
                @EntityResult(entityClass = ru.practicum.dto.ViewStats.class, fields = {
                        @FieldResult(name = "app", column = "app"),
                        @FieldResult(name = "uri", column = "uri"),
                        @FieldResult(name = "hits", column = "hits")})}
)
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}