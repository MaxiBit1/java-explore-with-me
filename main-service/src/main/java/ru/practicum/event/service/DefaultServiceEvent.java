package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.Constant;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.FollowSort;
import ru.practicum.event.repository.EventsRepository;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
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
        List<Event> events = eventsRepository
                .getEvents(PageRequest.of(from / size, size), users, states, categories, rangeStart, rangeEnd);
        Map<Long, Long> confrimeds = getConfirmedRequests(events);
        List<EventDtoOutFull> eventDtoOutFulls = events.stream()
                .map(EventMapper::toOutFull)
                .collect(Collectors.toList());
        eventDtoOutFulls.forEach(eventDtoOutFull -> {
                    eventDtoOutFull.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutFull.getId(), 0L));
                }

        );
        return eventDtoOutFulls;
    }


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
        Map<Long, Long> confrimeds = getConfirmedRequests(events);
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
        Map<Long, Long> confrimeds = getConfirmedRequests(List.of(event));
        eventDtoOutFull.setViews(views.getOrDefault(eventId, 0L));
        eventDtoOutFull.setConfirmedRequests(confrimeds.getOrDefault(eventId, 0L));
        return eventDtoOutFull;
    }

    @Override
    public List<EventDtoOutFull> getFollowEventsById(Long userId, Long followerId, String sort, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("From и size не могут быть меньше 0");
        }
        if (userId.equals(followerId)) {
            throw new ConflictException("Пользователь не может быть подписан сам на себя");
        }
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        User follower = userRepository
                .findById(followerId)
                .orElseThrow(() -> new NotFoundException(String.format("Подписанный пользователь с id = %d не найден", followerId)));

        if (!user.getFollower().contains(follower)) {
            throw new ConflictException("Пользователь не подписан на пользователя");
        }
        List<Event> events = eventsRepository.findAllByInitiatorIdAndState(followerId, EventState.PUBLISHED, PageRequest.of(from / size, size));
        events = getSortedFollowerEvents(events, sort);
        List<EventDtoOutFull> eventDtos = events.stream().map(EventMapper::toOutFull).collect(Collectors.toList());
        setViewsAndConfirmes(events, eventDtos);
        return eventDtos;
    }

    @Override
    public List<EventDtoOutFull> getFollowEvents(Long userId, String sort, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("From и size не могут быть меньше 0");
        }
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        if (user.getFollow().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> followers = user.getFollow().stream().map(User::getId).collect(Collectors.toList());
        List<Event> events = eventsRepository.findAllByStateAndInitiatorIdIn(EventState.PUBLISHED, followers, PageRequest.of(from / size, size));
        events = getSortedFollowerEvents(events, sort);
        List<EventDtoOutFull> eventDtos = events.stream().map(EventMapper::toOutFull).collect(Collectors.toList());
        setViewsAndConfirmes(events, eventDtos);
        return eventDtos;
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
            log.info("Step 1 oldEvent state = " + oldEvent.getState());
            switch (updateEventDtoIn.getStateAction()) {
                case PUBLISH_EVENT:
                    if (oldEvent.getState().equals(EventState.PENDING)) {
                        log.info("Step 2 oldEvent state = " + oldEvent.getState());
                        oldEvent.setState(EventState.PUBLISHED);
                        oldEvent.setPublishedOn(LocalDateTime.now());
                        log.info("Step 3 oldEvent state = " + oldEvent.getState());
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

    private Map<Long, Long> getConfirmedRequests(List<Event> eventsId) {
        List<Event> perEvents = eventsId.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
        List<Long> confirmedRequests = perEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> requestStats = new HashMap<>();
        if (!eventsId.isEmpty()) {
            requestRepository.findConfirmedRequests(confirmedRequests)
                    .forEach(stat -> requestStats.put(stat.getEventId(), stat.getConfirmedRequests()));
        }
        return requestStats;
    }

    private void setViewsAndConfirmes(List<Event> events, List<EventDtoOutFull> eventDtos) {
        log.info("view");
        Map<Long, Long> views = defaultStatClientService.getEventsView(events);
        Map<Long, Long> confrimeds = getConfirmedRequests(events);
        eventDtos.forEach(eventDtoOutShort -> {
                    eventDtoOutShort.setViews(views.getOrDefault(eventDtoOutShort.getId(), 0L));
                    eventDtoOutShort.setConfirmedRequests(confrimeds.getOrDefault(eventDtoOutShort.getId(), 0L));
                }

        );
    }

    private List<Event> getSortedFollowerEvents(List<Event> events, String sort) {
        Comparator<Event> comparator = (o1, o2) -> o2.getEventDate().compareTo(o1.getEventDate());
        if (FollowSort.valueOf(sort).equals(FollowSort.NEW)) {
            return events.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        return events.stream()
                .sorted(comparator.reversed())
                .collect(Collectors.toList());
    }
}

