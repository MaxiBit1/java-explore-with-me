package ru.practicum.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getParticipationRequests(Long userId, Long eventId);

    RequestStatusUpdateDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto);

    RequestDto updateRequestStatus(Long userId, Long requestId);

    List<RequestDto> getRequestsByUser(Long userId);
}
