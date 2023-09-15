package ru.practicum.complication.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.service.ComplicationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicControllerComp {

    private final ComplicationService complicationService;

    @GetMapping
    public ResponseEntity<List<ComplicationDtoOut>> getComlications(@RequestParam(required = false) boolean pinned,
                                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get complications from: " + from + " size: " + size + " pinned: " + pinned);
        return new ResponseEntity<>(complicationService.getComplications(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<ComplicationDtoOut> getCompById(@PathVariable("compId") Long id) {
        log.info("Get complication by id: " + id);
        return new ResponseEntity<>(complicationService.getComplication(id), HttpStatus.OK);
    }
}
