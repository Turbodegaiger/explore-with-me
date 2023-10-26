package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.LocationEntity;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
