package ru.practicum.client;

import io.micrometer.core.lang.Nullable;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatisticDto;

import java.util.List;
import java.util.Map;


public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    private static ResponseEntity<Object> buildStatisticResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return bodyBuilder.body(response.getBody());
        }
        return bodyBuilder.build();
    }

    private HttpHeaders defaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeader());

        ResponseEntity<Object> statisticResponse;
        try {
            if (parameters != null) {
                statisticResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statisticResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
        return buildStatisticResponse(statisticResponse);
    }

    public ResponseEntity<Object> get(@Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, "/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters, null);
    }

    public <T> ResponseEntity<Object> post(StatisticDto statisticDto) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, statisticDto);
    }
}
