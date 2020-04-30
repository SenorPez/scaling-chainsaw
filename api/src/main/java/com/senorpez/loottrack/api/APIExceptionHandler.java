package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handle405MethodNotAllow() {
        return ResponseEntity
                .status(METHOD_NOT_ALLOWED)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(METHOD_NOT_ALLOWED, "Method not allowed."));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    ResponseEntity<ErrorResponse> handle406NotAcceptable() {
        return ResponseEntity
                .status(NOT_ACCEPTABLE)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(NOT_ACCEPTABLE,
                        String.format("Accept header must be \"%s\"", MediaTypes.HAL_JSON.toString())));
    }

    private static class ErrorResponse {
        @JsonProperty
        private final int code;
        @JsonProperty
        private final String message;
        @JsonProperty
        private final String detail;

        private ErrorResponse(final HttpStatus httpStatus, final String detail) {
            this.code = httpStatus.value();
            this.message = httpStatus.getReasonPhrase();
            this.detail = detail;
        }
    }
}
