package edu.java.common.exception;

import edu.java.common.responseDto.ApiErrorResponse;


public class ApiErrorException extends RuntimeException{
    private final ApiErrorResponse errorResponse;

    public ApiErrorException(ApiErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ApiErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
