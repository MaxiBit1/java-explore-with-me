package ru.practicum.service;

import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticDtoEnd;

import java.util.List;

public interface StatisticService {
    StatisticDto save(StatisticDto dto);

    List<StatisticDtoEnd> getStats(String start, String end, List<String> uris, Boolean unique);
}
