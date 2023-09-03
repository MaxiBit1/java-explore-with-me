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
    public ResponseEntity<String> create(@Valid @RequestBody StatisticDto statisticDto) {
        log.info("Statistic from " + statisticDto.getIp() + " save");
        statisticService.save(statisticDto);
        return new ResponseEntity<>("Info save", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatisticDtoEnd>> getStatistic(@RequestParam("start") String start,
                                                              @RequestParam("end") String end,
                                                              @RequestParam(value = "uris", required = false) List<String> uris,
                                                              @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Get statistics");
        return new ResponseEntity<>(statisticService.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}
