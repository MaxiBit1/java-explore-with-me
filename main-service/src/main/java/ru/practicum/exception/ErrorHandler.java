package ru.practicum.exception;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.model.BadRequestException;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.Error;
import ru.practicum.exception.model.NotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleBadRequestException(MethodArgumentNotValidException e) {
        log.error("Bad request exception.");
        return responseEntity(new Error(HttpStatus.BAD_REQUEST,
                "Не соблюдены условия для запрошенной операции.", e.getMessage(), LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleBadRequestException(ValidationException e) {
        log.error("Bad request exception.");
        return responseEntity(new Error(HttpStatus.BAD_REQUEST,
                "Не все обязательные поля заполнены.", e.getMessage(), LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException e) {
        log.error("Bad request exception.");
        return responseEntity(new Error(HttpStatus.BAD_REQUEST,
                "Bad request exception", e.getMessage(), LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(final ConflictException e) {
        log.error("Conflict exception.");
        return responseEntity(new Error(HttpStatus.CONFLICT, "Запрос противоречит установленным ограничениям.",
                e.getMessage(), LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(final NotFoundException e) {
        log.error("Not found exception.");
        return responseEntity(new Error(HttpStatus.NOT_FOUND, "Требуемый объект не был найден.",
                e.getMessage(), LocalDateTime.now()));
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Throwable.class)
//    public ResponseEntity<Object> handleException(Throwable e) {
//        log.error("Internal server error.");
//        return responseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Недопустимый запрос.",
//                e.getMessage(), LocalDateTime.now()));
//    }

    private ResponseEntity<Object> responseEntity(Error apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
