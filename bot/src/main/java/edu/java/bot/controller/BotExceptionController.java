package edu.java.bot.controller;

import edu.java.common.exception.BadRequestException;
import edu.java.common.responseDto.ApiErrorResponse;
import edu.java.common.utils.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class BotExceptionController {
    private final static String BAD_REQUEST = "400 BAD_REQUEST";
    private final static String BAD_PARAMS = "Некорректные параметры запроса";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(MethodArgumentNotValidException ex) {
        return new ApiErrorResponse(
            BAD_PARAMS,
            ex.getStatusCode().toString(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(BadRequestException ex) {
        return new ApiErrorResponse(
            ex.getDescription(),
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
    }
}
