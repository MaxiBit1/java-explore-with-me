package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoIn;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.dto.UpdateEventDtoIn;
import ru.practicum.event.service.ServiceEvent;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class PrivateController {

    private final ServiceEvent serviceEvent;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventDtoOutFull> saveEvent(@PathVariable("userId") Long userId, @Valid @RequestBody EventDtoIn eventDtoIn) {
        log.info("Event with title: " + eventDtoIn.getTitle() + " private saved.");
        return new ResponseEntity<>(serviceEvent.save(userId, eventDtoIn), HttpStatus.CREATED);
    }

    @PatchMapping("{userId}/events/{eventId}")
    public ResponseEntity<EventDtoOutFull> updateEvent(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @Valid @RequestBody UpdateEventDtoIn updateEventDtoIn) {
        log.info("Event with id: " + eventId + " private updated.");
        return new ResponseEntity<>(serviceEvent.updateEventPrivate(userId, updateEventDtoIn, eventId), HttpStatus.OK);
    }

    @GetMapping("{userId}/events")
    public ResponseEntity<List<EventDtoOutFull>> getEvents(@PathVariable Long userId,
                                                           @RequestParam(value = "from", defaultValue = "0") int from,
                                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get events private userId: " + userId);
        return new ResponseEntity<>(serviceEvent.getUserEventsPrivate(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("{userId}/events/{eventId}")
    public ResponseEntity<EventDtoOutFull> getEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId) {
        log.info("Get event private userId: " + userId + " and eventId: " + eventId);
        return new ResponseEntity<>(serviceEvent.getUserEventPrivate(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> getParticipationRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии текущего пользователя");
        return new ResponseEntity<>(requestService.getParticipationRequests(userId, eventId), HttpStatus.OK);
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<RequestStatusUpdateDto> updateParticipationRequest(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateRequestDto) {
        log.info("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя {}",eventRequestStatusUpdateRequestDto);
        return new ResponseEntity<>(requestService.updateParticipationRequest(userId, eventId, eventRequestStatusUpdateRequestDto), HttpStatus.OK);
    }
}
