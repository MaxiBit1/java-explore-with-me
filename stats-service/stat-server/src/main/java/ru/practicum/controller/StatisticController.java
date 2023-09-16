package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticDtoEnd;
import ru.practicum.service.StatisticService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/hit")
    public ResponseEntity<StatisticDto> create(@Valid @RequestBody StatisticDto statisticDto) {
        log.info("Statistic ip: " + statisticDto.getIp() +
                " uri: " + statisticDto.getUri() +
                " app: " + statisticDto.getApp() +
                " timestamp " + statisticDto.getTimestamp() + " save");
        return new ResponseEntity<>(statisticService.save(statisticDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatisticDtoEnd>> getStatistic(@RequestParam(value = "start", defaultValue = "0") String start,
                                                              @RequestParam(value = "end", defaultValue = "0") String end,
                                                              @RequestParam(value = "uris", required = false) List<String> uris,
                                                              @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Get statistics");
        return new ResponseEntity<>(statisticService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}
