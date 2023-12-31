package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticDtoEnd;
import ru.practicum.exception.model.BadRequest;
import ru.practicum.mapper.Mapper;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DefaultStatisticService implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public StatisticDto save(StatisticDto dto) {
        return Mapper.toDto(statisticRepository.save(Mapper.toEntity(dto)));
    }

    @Override
    public List<StatisticDtoEnd> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {
        if (startStr.equals("0") || endStr.equals("0")) {
            throw new BadRequest("Time not null");
        }
        try {
            LocalDateTime start = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (end.isBefore(start)) {
                throw new BadRequest("End not be before start");
            }
            if (uris == null) {
                return unique ? statisticRepository.findAllByTimestampBetweenDistinct(start, end).stream()
                        .sorted(((o1, o2) -> o2.getHits().compareTo(o1.getHits())))
                        .collect(Collectors.toList()) :
                        statisticRepository.findAllByTimestampBetween(start, end).stream()
                                .sorted(((o1, o2) -> o2.getHits().compareTo(o1.getHits())))
                                .collect(Collectors.toList());
            } else {
                return unique ? statisticRepository.findAllByTimestampBetweenDistinct(start, end).stream()
                        .filter(stat -> uris.contains(stat.getUri()))
                        .sorted(((o1, o2) -> o2.getHits().compareTo(o1.getHits())))
                        .collect(Collectors.toList()) :
                        statisticRepository.findAllByTimestampBetween(start, end).stream()
                                .filter(stat -> uris.contains(stat.getUri()))
                                .sorted(((o1, o2) -> o2.getHits().compareTo(o1.getHits())))
                                .collect(Collectors.toList());
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Parse date time error");
        }
    }
}
