package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticDtoEnd;
import ru.practicum.mapper.Mapper;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DefaultStatisticService implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public void save(StatisticDto dto) {
        statisticRepository.save(Mapper.ToEntity(dto));
    }

    @Override
    public List<StatisticDtoEnd> getStats(String startStr, String endStr, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (uris == null) {
            if (unique) {
                return statisticRepository.findAllByTimestampBetweenDistinct(start, end);
            } else {
                return statisticRepository.findAllByTimestampBetween(start, end);
            }

        } else {
            if (unique) {
                return statisticRepository.findAllByTimestampBetweenDistinctAndUris(uris, start, end);
            } else {
                return statisticRepository.findAllByTimestampBetweenAndUris(uris, start, end);
            }
        }
//        for (String uri : uris) {
////            if (!unique && start.equals(end)) {
////                views.add(Mapper.toView(statisticRepository.findAllByUri(uri)));
////            } else if (unique && start.equals(end)) {
////                views.add(Mapper.toView(statisticRepository.findAllByUriDistinct(uri)));
////            } else if (!unique && !start.equals(end)) {
////                views.add(Mapper.toView(statisticRepository.findAllByUriAndTimestampBetween(uri, start, end)));
////            } else if (unique && !start.equals(end)) {
////                views.add(Mapper.toView(statisticRepository.findAllByUriAndBetweenDistinct(uri, start, end)));
////            }
//            if(ur)
//        }
    }
}
