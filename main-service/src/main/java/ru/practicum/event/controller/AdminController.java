package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constant;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.dto.UpdateEventDtoIn;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.ServiceEvent;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ServiceEvent serviceEvent;

    @GetMapping("/events")
    public ResponseEntity<List<EventDtoOutFull>> getAdminEvent(@RequestParam(required = false) List<Long> users,
                                                               @RequestParam(required = false) List<EventState> states,
                                                               @RequestParam(required = false) List<Long> categories,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeStart,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeEnd,
                                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get event admin from: " + from + " size: " + size);
        return new ResponseEntity<>(serviceEvent.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size), HttpStatus.OK);
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventDtoOutFull> updateAdminEvent(@PathVariable Long eventId,
                                                            @Valid @RequestBody UpdateEventDtoIn updateEventDtoIn) {
        log.info("Update admin event: " + eventId);
        return new ResponseEntity<>(serviceEvent.updateEventAdmin(eventId, updateEventDtoIn), HttpStatus.OK);
    }
}
