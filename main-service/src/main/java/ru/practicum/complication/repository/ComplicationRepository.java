package ru.practicum.complication.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.complication.model.Complication;

import java.util.List;

public interface ComplicationRepository extends JpaRepository<Complication, Long> {
    List<Complication> findAllByPinned(Boolean pinned, Pageable page);
}
