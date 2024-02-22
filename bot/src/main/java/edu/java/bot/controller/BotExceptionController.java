package edu.java.bot.controller;

import edu.java.bot.dto.ApiErrorResponse;
import edu.java.bot.exception.UpdateAlreadyExistException;
import edu.java.bot.utils.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class BotExceptionController {
    private final static String BAD_REQUEST = "400 BAD_REQUEST";
    private final static String BAD_PARAMS = "Некорректные параметры запроса";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(MethodArgumentNotValidException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            BAD_PARAMS,
            ex.getStatusCode().toString(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(UpdateAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(UpdateAlreadyExistException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Update уже существует",
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
