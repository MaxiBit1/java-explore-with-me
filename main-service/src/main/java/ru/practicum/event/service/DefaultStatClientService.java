package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.Constant;
import ru.practicum.client.StatisticClient;
import ru.practicum.dto.StatisticDto;
import ru.practicum.dto.StatisticDtoEnd;
import ru.practicum.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DefaultStatClientService {

    private final StatisticClient statisticClient;
    private final ObjectMapper objectMapper;

    public Map<Long, Long> getEventsView(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<Event> publishedEvents = getPublished(events);
        if (publishedEvents.isEmpty()) {
            return views;
        }
        Optional<LocalDateTime> minPublishedOn = getMinPublishedOn(publishedEvents);
        if (minPublishedOn.isEmpty()) {
            return views;
        }
        LocalDateTime start = minPublishedOn.get();
        LocalDateTime end = LocalDateTime.now();
        List<String> urls = urls(publishedEvents);
        List<StatisticDtoEnd> stats = getStatistic(start, end, urls, true);
        stats.forEach(statisticDtoEnd -> {
            Long eventId = Long.parseLong(statisticDtoEnd.getUri()
                    .split("/", 0)[2]);
            views.put(eventId, views.getOrDefault(eventId, 0L) + statisticDtoEnd.getHits());
        });
        return views;
    }

    public void createHit(HttpServletRequest request) {
        StatisticDto statisticDto = StatisticDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .timestamp(LocalDateTime.now().format(Constant.FORMATTER))
                .build();
        statisticClient.postHit(statisticDto);
    }

    private List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());
    }

    private Optional<LocalDateTime> getMinPublishedOn(List<Event> publishedEvents) {
        return publishedEvents.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
    }

    private List<String> urls(List<Event> publishedEvents) {
        return publishedEvents.stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());
    }

    private List<StatisticDtoEnd> getStatistic(LocalDateTime start, LocalDateTime end, List<String> urls, Boolean unique) {
        ResponseEntity<Object> response = statisticClient.getStats(start, end, urls, unique);
        try {
            return Arrays.asList(objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), StatisticDtoEnd[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
