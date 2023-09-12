package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.Constant;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventsRepository;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class DefaultServiceEvent implements ServiceEvent {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DefaultStatClientService defaultStatClientService;
    private final RequestRepository requestRepository;

    @Override
    public EventDtoOutFull save(Long userId, EventDtoIn eventDtoIn) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        Category category = categoryRepository
                .findById(eventDtoIn.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найден", eventDtoIn.getCategory())));
        checkEvent(eventDtoIn);
        Event event = EventMapper.toEntity(eventDtoIn, category, user);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        checkTimeEvent(event);
        return EventMapper.toOutFull(eventsRepository.save(event));
    }

    @Override
    public EventDtoOutFull updateEventPrivate(Long userId, UpdateEventDtoIn updateEventDtoIn, Long eventId) {
        Event oldEvent = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        if (!Objects.equals(oldEvent.getInitiator().getId(), userId)) {
            throw new ConflictException("Невозможно получить полную информацию о событии.");
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события.");
        }
        if (updateEventDtoIn.getEventDate() != null) {
            oldEvent.setEventDate(LocalDateTime.parse(updateEventDtoIn.getEventDate(), Constant.FORMATTER));
            checkTimeEvent(oldEvent);
        }
        if (updateEventDtoIn.getStateAction() != null) {
            switch (updateEventDtoIn.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        return EventMapper.toOutFull(oldEvent);
    }

    @Override
    public EventDtoOutFull getUserEventPrivate(Long userId, Long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Невозможно получить полную информацию о событии.");
        }
        return EventMapper.toOutFull(event);
    }

    @Override
    public List<EventDtoOutFull> getUserEventsPrivate(Long userId, int from, int size) {
        return eventsRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size)).stream()
                .map(EventMapper::toOutFull)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoOutFull updateEventAdmin(Long eventId, UpdateEventDtoIn updateEventDtoIn) {
        Event oldEvent = eventsRepository.findById(eventId).orElseThrow();
        return updateEventUtil(oldEvent, updateEventDtoIn);
    }

    @Override
    public List<EventDtoOutFull> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        checkStartEnd(rangeStart, rangeEnd);
        List<EventDtoOutFull> events = eventsRepository
                .getEvents(PageRequest.of(from / size, size), users, states, categories, rangeStart, rangeEnd)
                .stream()
                .map(EventMapper::toOutFull)
                .collect(Collectors.toList());
        Map<Long, Long> confrimeds = getConfirmedRequests(events.stream().map(EventDtoOutFull::getId).collect(Collectors.toList()));
        events.forEach(eventDtoOutFull -> {
                    eventDtoOutFull.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutFull.getId(), 0L));
                }

        );
        return events;
    }

//    @Override
//    public List<EventDtoOutShort> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {
//        checkStartEnd(rangeStart, rangeEnd);
//        defaultStatClientService.createHit(httpServletRequest);
//        List<Event> events = eventsRepository.findAllByText(text);
//        if (categories != null) {
//            events = events.stream()
//                    .filter(eventDtoOutFull -> categories.contains(eventDtoOutFull.getCategory().getId()))
//                    .collect(Collectors.toList());
//        }
//        if (paid != null) {
//            events = events.stream()
//                    .filter(eventDtoOutFull -> paid.equals(eventDtoOutFull.getPaid()))
//                    .collect(Collectors.toList());
//        }
//        if (rangeStart != null && rangeEnd != null) {
//            events = eventsRepository.findAllByText(text).stream()
//                    .filter(event -> (
//                            event.getEventDate().isAfter(rangeStart)
//                                    && event.getEventDate().isBefore(rangeEnd)
//                    ))
//                    .collect(Collectors.toList());
//        }
//        List<EventDtoOutShort> eventDtos = events.stream()
//                .map(EventMapper::toOutShort)
//                .collect(Collectors.toList());
//        if (onlyAvailable) {
//            List<Long> idsConfirm = requestRepository.findAll().stream()
//                    .filter(request -> request.getStatus().equals(StatusRequest.CONFIRMED))
//                    .map(request -> request.getEvent().getId())
//                    .collect(Collectors.toList());
//            eventDtos = eventDtos.stream()
//                    .filter(eventDtoOutShort -> idsConfirm.contains(eventDtoOutShort.getId()))
//                    .collect(Collectors.toList());
//
//        }
//        Map<Long, Long> views = defaultStatClientService.getEventsView(events);
//        Map<Long, Long> confrimeds = getConfirmedRequests(events.stream().map(Event::getId).collect(Collectors.toList()));
//        eventDtos.forEach(eventDtoOutShort -> {
//                    eventDtoOutShort.setViews(views.getOrDefault(eventDtoOutShort.getId(), 0L));
//                    eventDtoOutShort.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutShort.getId(), 0L));
//                }
//
//        );
//        if (sort != null) {
//            switch (SortEnum.valueOf(sort)) {
//                case EVENT_DATE:
//                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventDtoOutShort::getEventDate)).collect(Collectors.toList());
//                    break;
//                case VIEWS:
//                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventDtoOutShort::getViews)).collect(Collectors.toList());
//                    break;
//            }
//        }
//        Pageable pageable = PageRequest.of(from, size);
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), eventDtos.size());
//        return new PageImpl<>(eventDtos.subList(start, end), pageable, eventDtos.size()).getContent();
//    }

    @Override
    public List<EventDtoOutShort> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {
        checkStartEnd(rangeStart, rangeEnd);
        defaultStatClientService.createHit(httpServletRequest);
        List<Event> events = new ArrayList<>(eventsRepository.searchEvent(text, categories, paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                PageRequest.of(from / size, size)));
        List<EventDtoOutShort> eventDtos = events.stream()
                .map(EventMapper::toOutShort)
                .collect(Collectors.toList());

        Map<Long, Long> views = defaultStatClientService.getEventsView(events);
        Map<Long, Long> confrimeds = getConfirmedRequests(events.stream().map(Event::getId).collect(Collectors.toList()));
        eventDtos.forEach(eventDtoOutShort -> {
                    eventDtoOutShort.setViews(views.getOrDefault(eventDtoOutShort.getId(), 0L));
                    eventDtoOutShort.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutShort.getId(), 0L));
                }

        );
        if (sort != null) {
            switch (SortEnum.valueOf(sort)) {
                case EVENT_DATE:
                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventDtoOutShort::getEventDate)).collect(Collectors.toList());
                    break;
                case VIEWS:
                    eventDtos = eventDtos.stream().sorted(Comparator.comparing(EventDtoOutShort::getViews)).collect(Collectors.toList());
                    break;
            }
        }

        return eventDtos;
    }

    @Override
    public EventDtoOutFull getEventPublic(Long eventId, HttpServletRequest request) {
        defaultStatClientService.createHit(request);
        Event event = eventsRepository.findById(eventId).orElseThrow();
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие не найдено");
        }
        EventDtoOutFull eventDtoOutFull = EventMapper.toOutFull(event);
        Map<Long, Long> views = defaultStatClientService.getEventsView(List.of(event));
        Map<Long, Long> confrimeds = getConfirmedRequests(List.of(event.getId()));
        eventDtoOutFull.setViews(views.getOrDefault(eventId, 0L));
        eventDtoOutFull.setConfirmedRequests(confrimeds.getOrDefault(eventId, 0L));
        return eventDtoOutFull;
    }

    private void checkEvent(EventDtoIn eventDtoIn) {
        if (eventDtoIn.getPaid() == null) {
            eventDtoIn.setPaid(false);
        }
        if (eventDtoIn.getRequestModeration() == null) {
            eventDtoIn.setRequestModeration(true);
        }
        if (eventDtoIn.getParticipantLimit() == null) {
            eventDtoIn.setParticipantLimit(0);
        }
    }

    private void checkTimeEvent(Event event) {
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(2);
        if (event.getEventDate().isBefore(localDateTime)) {
            throw new BadRequestException("Дата события должна быть не менее чем через два часа.");
        }
    }

    private EventDtoOutFull updateEventUtil(Event oldEvent, UpdateEventDtoIn updateEventDtoIn) {
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события.");
        }
        if (updateEventDtoIn.getEventDate() != null) {
            oldEvent.setEventDate(LocalDateTime.parse(updateEventDtoIn.getEventDate(), Constant.FORMATTER));
            checkTimeEvent(oldEvent);
        }
        if (updateEventDtoIn.getStateAction() != null) {
            switch (updateEventDtoIn.getStateAction()) {
                case PUBLISH_EVENT:
                    if (oldEvent.getState().equals(EventState.PENDING)) {
                        oldEvent.setState(EventState.PUBLISHED);
                        oldEvent.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания.");
                    }
                    break;
                case REJECT_EVENT:
                    if (oldEvent.getState().equals(EventState.PENDING)) {
                        oldEvent.setState(EventState.CANCELED);
                    } else {
                        throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
                    }
                    break;
            }
        }
        if (updateEventDtoIn.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEventDtoIn.getAnnotation());
        }
        if (updateEventDtoIn.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDtoIn.getCategory()).orElseThrow();
            oldEvent.setCategory(category);
        }
        if (updateEventDtoIn.getDescription() != null) {
            oldEvent.setDescription(updateEventDtoIn.getDescription());
        }
        if (updateEventDtoIn.getLocation() != null) {
            oldEvent.setLat(updateEventDtoIn.getLocation().getLat());
            oldEvent.setLon(updateEventDtoIn.getLocation().getLon());
        }
        if (updateEventDtoIn.getPaid() != null) {
            oldEvent.setPaid(updateEventDtoIn.getPaid());
        }
        if (updateEventDtoIn.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEventDtoIn.getParticipantLimit());
        }
        if (updateEventDtoIn.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEventDtoIn.getRequestModeration());
        }
        if (updateEventDtoIn.getTitle() != null) {
            oldEvent.setTitle(updateEventDtoIn.getTitle());
        }

        return EventMapper.toOutFull(eventsRepository.save(oldEvent));
    }

    private void checkStartEnd(LocalDateTime start, LocalDateTime end) {
        if (end != null && start != null) {
            if (end.isBefore(start)) {
                throw new BadRequestException("Время окончания не должно быть раньше времени начала.");
            }
        }
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventsId) {
        List<Request> confirmedRequests = requestRepository.findAll().stream()
                .filter(request -> request.getStatus().equals(StatusRequest.CONFIRMED)
                        && eventsId.contains(request.getEvent().getId()))
                .collect(Collectors.toList());

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }
}

