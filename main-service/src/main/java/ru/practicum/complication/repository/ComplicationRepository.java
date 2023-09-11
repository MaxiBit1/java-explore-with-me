package ru.practicum.complication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.complication.model.Complication;

public interface ComplicationRepository extends JpaRepository<Complication, Long> {
}
