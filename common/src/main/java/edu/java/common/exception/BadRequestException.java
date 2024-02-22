package edu.java.common.exception;

import lombok.Getter;


@Getter
public class BadRequestException extends RuntimeException{

    private final String description;
    public BadRequestException(String message, String description) {
        super(message);
        this.description = description;
    }
}
