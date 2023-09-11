package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constant;
import ru.practicum.event.dto.EventDtoOutFull;
import ru.practicum.event.dto.EventDtoOutShort;
import ru.practicum.event.service.ServiceEvent;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicController {
    private final ServiceEvent serviceEvent;

    @GetMapping
    public ResponseEntity<List<EventDtoOutShort>> getEventsPub(@RequestParam(required = false) String text,
                                                               @RequestParam(required = false) List<Long> categoryIds,
                                                               @RequestParam(required = false) Boolean paid,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeStart,
                                                               @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeEnd,
                                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                               @RequestParam(required = false) String sort,
                                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                               @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                               HttpServletRequest httpServletRequest) {
        log.info("Get public events from: " + from + " size: " + size);
        return new ResponseEntity<>(serviceEvent.getEventsPublic(text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDtoOutFull> getEventPub(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        log.info("Get public event id: " + id);
        return new ResponseEntity<>(serviceEvent.getEventPublic(id, httpServletRequest), HttpStatus.OK);
    }
}
