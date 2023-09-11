package ru.practicum.event.service;

import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.dto.EventDtoOutShort;
import ru.practicum.event.dto.UpdateEventDtoIn;
import ru.practicum.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface ServiceEvent {
    EventDtoOutFull save(Long userId, EventDtoIn eventDtoIn);

    EventDtoOutFull updateEventPrivate(Long userId, UpdateEventDtoIn updateEventDtoIn, Long eventId);

    EventDtoOutFull getUserEventPrivate(Long userId, Long eventId);

    List<EventDtoOutFull> getUserEventsPrivate(Long userId, int from, int size);

    EventDtoOutFull updateEventAdmin(Long eventId, UpdateEventDtoIn updateEventDtoIn);

    List<EventDtoOutFull> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventDtoOutShort> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest);

    EventDtoOutFull getEventPublic(Long eventId, HttpServletRequest request);


}



