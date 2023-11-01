package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Like;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByEventIdAndIsLiked(Long eventId, boolean isLike);

    Optional<Like> findByEventIdAndUserId(Long eventId, Long userId);
}
