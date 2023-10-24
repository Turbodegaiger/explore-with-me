package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findAllByRequesterId(Long userId);

    Iterable<Request> findAllByEventId(Long eventId);

    List<Request> findByIdInOrderByCreatedAsc(List<Long> requestIds);
}