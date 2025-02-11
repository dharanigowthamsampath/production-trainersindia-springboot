package com.trainersindia.portal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private final HttpStatus status;

    public UserException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
} 