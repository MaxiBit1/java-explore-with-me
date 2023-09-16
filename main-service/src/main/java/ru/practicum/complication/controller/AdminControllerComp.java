package ru.practicum.complication.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.complication.dto.ComplicationDtoIn;
import ru.practicum.complication.dto.ComplicationDtoOut;
import ru.practicum.complication.dto.UpdateComplicationDtoIn;
import ru.practicum.complication.service.ComplicationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminControllerComp {

    private final ComplicationService complicationService;

    @PostMapping
    public ResponseEntity<ComplicationDtoOut> save(@Valid @RequestBody ComplicationDtoIn complicationDtoIn) {
        log.info("Complication with title: " + complicationDtoIn.getTitle() + " saved");
        return new ResponseEntity<>(complicationService.save(complicationDtoIn), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComp(@PathVariable("compId") long id) {
        log.info("Complication with id: " + id + " deleted");
        complicationService.deleteComplication(id);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<ComplicationDtoOut> update(@PathVariable("compId") Long id, @Valid @RequestBody UpdateComplicationDtoIn updateComplicationDtoIn) {
        log.info("Complication with id: " + id + " updated");
        return new ResponseEntity<>(complicationService.updateComplication(id, updateComplicationDtoIn), HttpStatus.OK);
    }
}
