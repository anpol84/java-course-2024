package edu.java.bot.controller;

import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.exception.BadRequestException;
import edu.java.bot.utils.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class BotExceptionController {
    private final static String BAD_REQUEST = "400 BAD_REQUEST";
    private final static String BAD_PARAMS = "Invalid request parameters";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(MethodArgumentNotValidException ex) {
        return new ApiErrorResponse().setDescription(BAD_PARAMS)
            .setCode(ex.getStatusCode().toString())
            .setExceptionName(ex.getClass().getSimpleName())
            .setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(BadRequestException ex) {
        return new ApiErrorResponse().setDescription(ex.getDescription())
            .setCode(BAD_REQUEST)
            .setExceptionName(ex.getClass().getSimpleName())
            .setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }
}
