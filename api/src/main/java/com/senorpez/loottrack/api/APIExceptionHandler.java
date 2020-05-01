package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(CampaignNotFoundException.class)
    ResponseEntity<ErrorResponse> handleAPIObjectNotFound(final Exception e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handle405MethodNotAllowed() {
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

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handle500ServerError(Exception ex, HttpServletRequest request) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR,
                        String.format(">>>>>Internal server error occurred.<<<<<\r\n " +
                                ">>>>>Please report to https://github.com/SenorPez/scaling-chainsaw/issues<<<< \r\n " +
                                "%s \r\n %s", stringWriter.toString(), request.getRequestURI())));
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
