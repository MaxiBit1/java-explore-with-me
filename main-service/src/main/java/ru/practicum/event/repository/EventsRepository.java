package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long id, Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE (COALESCE(:userIds, -1) IS -1 OR e.initiator.id IN :userIds) " +
            "AND (COALESCE(:states, ' ') IS ' ' OR e.state IN :states) " +
            "AND (COALESCE(:categoryIds, -1) IS -1 OR e.category.id IN :categoryIds) " +
            "AND (COALESCE(:rangeStart, ' ') IS ' ' OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, ' ') IS ' ' OR e.eventDate <= :rangeEnd) ")
    List<Event> getEvents(Pageable pageable,
                          @Param("userIds") List<Long> userIds,
                          @Param("states") List<EventState> states,
                          @Param("categoryIds") List<Long> categoryIds,
                          @Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd
    );

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, ' ') IS ' ' OR (LOWER(e.annotation) LIKE LOWER(concat('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(concat('%', :text, '%')))) " +
            "AND (COALESCE(:categoryIds, -1) IS -1 OR e.category.id IN :categoryIds) " +
            "AND (COALESCE(:paid, FALSE) IS FALSE OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, ' ') IS ' ' OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, ' ') IS ' ' OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.id IN " +
            "(SELECT r.event.id " +
            "FROM Request r " +
            "WHERE r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id " +
            "HAVING e.participantLimit - count(id) > 0 " +
            "ORDER BY count(r.id))) ")
    List<Event> searchEvent(@Param("text") String text,
                            @Param("categoryIds") List<Long> categoryIds,
                            @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable, Pageable pageable);

}
