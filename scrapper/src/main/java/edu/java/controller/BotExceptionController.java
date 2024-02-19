package edu.java.controller;

import edu.java.dto.ApiErrorResponse;
import edu.java.exception.BadPathParameterException;
import edu.java.exception.ChatAlreadyExistException;
import edu.java.exception.ChatNotExistException;
import edu.java.exception.LinkAlreadyExistException;
import edu.java.exception.LinkNotExistException;
import edu.java.utils.StackTraceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
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

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(NumberFormatException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            BAD_PARAMS,
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ChatAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ChatAlreadyExistException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Чат уже существует",
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(LinkAlreadyExistException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(LinkAlreadyExistException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Ссылка уже существует",
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({ChatNotExistException.class, LinkNotExistException.class})
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(Exception ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            "Ресурс не существует",
            "404 NOT_FOUND",
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadPathParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(BadPathParameterException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            BAD_PARAMS,
            BAD_REQUEST,
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(MissingRequestHeaderException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
            BAD_PARAMS,
            ex.getStatusCode().toString(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            StackTraceUtil.getStringStakeTrace(ex));
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
}
