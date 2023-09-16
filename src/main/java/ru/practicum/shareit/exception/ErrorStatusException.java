package ru.practicum.shareit.exception;


public class ErrorStatusException extends RuntimeException {

    public ErrorStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorStatusException() {

    }
}
