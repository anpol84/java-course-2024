package edu.java.controller;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.serviceDto.ApiErrorResponse;
import edu.java.utils.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


@RestControllerAdvice
public class ScrapperExceptionController {
    private final static String BAD_REQUEST = "400 BAD_REQUEST";
    private final static String BAD_PARAMS = "Incorrect request parameters";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(MethodArgumentNotValidException ex) {
        return new ApiErrorResponse().setDescription(BAD_PARAMS).setCode(ex.getStatusCode().toString())
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(HandlerMethodValidationException ex) {
        return new ApiErrorResponse().setDescription(BAD_PARAMS).setCode(ex.getStatusCode().toString())
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(NumberFormatException ex) {
        return new ApiErrorResponse().setDescription(BAD_PARAMS).setCode(BAD_REQUEST)
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(BadRequestException ex) {
        return new ApiErrorResponse().setDescription(ex.getDescription()).setCode(BAD_REQUEST)
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleResponseStatusException(NotFoundException ex) {
        return new ApiErrorResponse().setDescription(ex.getDescription()).setCode("404 NOT_FOUND")
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleResponseStatusException(MissingRequestHeaderException ex) {
        return new ApiErrorResponse().setDescription(BAD_PARAMS).setCode(ex.getStatusCode().toString())
            .setExceptionName(ex.getClass().getSimpleName()).setExceptionMessage(ex.getMessage())
            .setStacktrace(StackTraceUtil.getStringStakeTrace(ex));
    }
}
