package ru.practicum.complication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.dto.UpdateComplicationDtoIn;
import ru.practicum.complication.mapper.ComplicationMapper;
import ru.practicum.complication.model.Complication;
import ru.practicum.complication.repository.ComplicationRepository;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventsRepository;
import ru.practicum.event.service.DefaultStatClientService;
import ru.practicum.exception.model.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultComplicationService implements ComplicationService {

    private final ComplicationRepository complicationRepository;
    private final EventsRepository eventsRepository;
    private final DefaultStatClientService defaultStatClientService;


    @Override
    public ComplicationDtoOut save(ComplicationDtoIn complicationDtoIn) {
        List<Event> events = eventsRepository.findAllById(complicationDtoIn.getEvents() == null ? Collections.emptyList() : complicationDtoIn.getEvents());
        if (complicationDtoIn.getPinned() == null) {
            complicationDtoIn.setPinned(false);
        }
        Complication complication = complicationRepository.save(ComplicationMapper.toEntity(complicationDtoIn, events));
        return setComplicationViews(events, complication);
    }

    @Override
    public void deleteComplication(Long complicationId) {
        Complication complication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", complicationId)));
        complicationRepository.delete(complication);
    }

    @Override
    public ComplicationDtoOut updateComplication(Long complicationId, UpdateComplicationDtoIn complicationDtoIn) {
        Complication oldComplication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", complicationId)));
        if (complicationDtoIn.getEvents() != null) {
            oldComplication.setEvents(eventsRepository.findAllById(complicationDtoIn.getEvents()));
        }
        return setComplicationViews(oldComplication.getEvents(), oldComplication);
    }

    @Override
    public List<ComplicationDtoOut> getComplications(Boolean pinned, Integer from, Integer size) {
        if (pinned == null) {
            return complicationRepository
                    .findAll(PageRequest.of(from / size, size))
                    .stream()
                    .map(complic -> setComplicationViews(complic.getEvents(), complic))
                    .collect(Collectors.toList());
        }
        return complicationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size))
                .stream()
                .map(complic -> setComplicationViews(complic.getEvents(), complic))
                .collect(Collectors.toList());
    }

    @Override
    public ComplicationDtoOut getComplication(Long complicationId) {
        Complication complication = complicationRepository.findById(complicationId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", complicationId)));
        return setComplicationViews(complication.getEvents(), complication);
    }

    private ComplicationDtoOut setComplicationViews(List<Event> events, Complication complication) {
        List<EventDtoOutFull> eventFills = new ArrayList<>();
        if (!events.isEmpty()) {
            Map<Long, Long> views = defaultStatClientService.getEventsView(events);
            eventFills = events.stream()
                    .map(EventMapper::toOutFull)
                    .collect(Collectors.toList());
            eventFills.forEach(
                    eventDtoOutFull -> {
                        eventDtoOutFull.setViews(views.getOrDefault(eventDtoOutFull.getId(), 0L));
                    }
            );
        }
        return ComplicationMapper.complicationDtoOut(complication, eventFills);
    }
}
