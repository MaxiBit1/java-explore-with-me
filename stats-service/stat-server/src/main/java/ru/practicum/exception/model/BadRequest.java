package ru.practicum.exception.model;

public class BadRequest extends RuntimeException{
    public BadRequest(String message) {
        super(message);
    }
}
