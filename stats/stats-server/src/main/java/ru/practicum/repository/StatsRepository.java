package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(nativeQuery = true, name = "ViewStats")
    List<ViewStats> findAllUriIsIn(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true, name = "ViewStatsDistinct")
    List<ViewStats> findAllUriIsInDistinct(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true, name = "ViewStatsAllUri")
    List<ViewStats> findAllUri(LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true, name = "ViewStatsAllUriDistinct")
    List<ViewStats> findAllUriDistinct(LocalDateTime start, LocalDateTime end);

}
