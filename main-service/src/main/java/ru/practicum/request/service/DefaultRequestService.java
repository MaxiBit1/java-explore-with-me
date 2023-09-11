package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventsRepository;
import ru.practicum.event.service.DefaultStatClientService;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.StatusRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultRequestService implements RequestService {

    private final RequestRepository requestRepository;
    private final EventsRepository eventRepository;
    private final UserRepository userRepository;

    private final DefaultStatClientService defaultStatClientService;

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        if (eventId == null || userId == null) {
            throw new BadRequestException("Id события и/или пользователя не должно быть равно нулю");
        }
        if (requestRepository.findAllByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new ConflictException("Нельзя добавить повторный запрос.");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может отправлять запрос на участие.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Данное событие не опубликовано.");
        }


        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(countEvents(eventId))) {
            throw new ConflictException("Лимит превышен");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);

        if (event.getRequestModeration().equals(true) && event.getParticipantLimit() > 0) {
            request.setStatus(StatusRequest.PENDING);
        } else {
            request.setStatus(StatusRequest.CONFIRMED);
        }
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getParticipationRequests(Long userId, Long eventId) {
        return requestRepository.findAll().stream()
                .filter(request -> request.getRequester().getId().equals(userId) && request.getEvent().getId().equals(eventId))
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));


        List<Long> requestIds = eventRequestStatusUpdateDto.getRequestIds();
        StatusRequest status = eventRequestStatusUpdateDto.getStatus();

        List<Request> requests = requestRepository.findAll().stream()
                .filter(request -> request.getEvent().getId().equals(eventId))
                .collect(Collectors.toList());

        List<RequestDto> confirmedList = new ArrayList<>();
        List<RequestDto> rejectedList = new ArrayList<>();

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(countEvents(eventId))) {
            throw new ConflictException("Лимит превышен");
        }

        if (requestIds != null) {
            requestIds.forEach(id -> {
                Request request = requests.stream()
                        .filter(request1 -> request1.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", id)));
                if (request.getStatus().equals(StatusRequest.PENDING)) {
                    throw new ConflictException("Запрос невозможно подтвердить.");
                }
                if (status.equals(StatusRequest.CONFIRMED)) {
                    request.setStatus(StatusRequest.CONFIRMED);
                    confirmedList.add(RequestMapper.toDto(requestRepository.save(request)));
                } else {
                    request.setStatus(StatusRequest.REJECTED);
                    rejectedList.add(RequestMapper.toDto(requestRepository.save(request)));
                }
            });
        }
        eventRepository.save(event);

        return new RequestStatusUpdateDto(confirmedList, rejectedList);
    }

    @Override
    public RequestDto updateRequestStatus(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", requestId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        request.setStatus(StatusRequest.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        return requestRepository.findAll().stream()
                .filter(request -> request.getRequester().getId().equals(userId))
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private int countEvents(Long eventId) {
        return (int) requestRepository.findAll().stream()
                .filter(request -> Objects.equals(request.getEvent().getId(), eventId)
                        && request.getStatus() == StatusRequest.CONFIRMED)
                .count();
    }
}
