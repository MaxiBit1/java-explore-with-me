package ru.practicum.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Error {

    private HttpStatus status;

    private final String reason;

    private final String message;

    private final LocalDateTime timestamp;

}
