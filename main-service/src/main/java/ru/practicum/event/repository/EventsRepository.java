package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long id, Pageable page);

    @Query("select e " +
            "from Event as e where e.state = 'PUBLISHED'" +
            "and (lower(e.annotation) like lower(concat('%', '?1', '%')) or lower(e.description) like lower(concat('%', '?1', '%'))) ")
    List<Event> findAllByText(String text);

}
