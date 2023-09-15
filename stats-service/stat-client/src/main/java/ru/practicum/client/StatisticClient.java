package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatisticClient extends BaseClient {
    @Autowired
    public StatisticClient(@Value("${stats-server.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + ""))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postHit(StatisticDto inDto) {
        return post("/hit", inDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String path = getStatisticPath(uris);
        Map<String, Object> parameters = getStatsParameters(start, end, uris, unique);
        return get(path, parameters);
    }

    private String getStatisticPath(List<String> uris) {
        if (uris == null) {
            return "/stats?start={start}&end={end}&unique={unique}";
        } else {
            return "/stats?start={start}&end={end}&unique={unique}&uris={uris}";
        }
    }

    private Map<String, Object> getStatsParameters(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            return Map.of(
                    "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "unique", unique
            );
        } else {
            return Map.of(
                    "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "unique", unique,
                    "uris", String.join(", ", uris)
            );
        }
    }
}
