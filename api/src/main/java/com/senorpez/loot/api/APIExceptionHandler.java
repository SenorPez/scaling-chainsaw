package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senorpez.loot.api.exception.CampaignNotFoundException;
import com.senorpez.loot.api.exception.CharacterNotFoundException;
import com.senorpez.loot.api.exception.ItemNotFoundException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
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
    // TODO: 7/6/2020 Handle JSON deserialization errors (as proper HTTP code).
    // MismatchedInputException
    // JsonParseException
    
    @ExceptionHandler({
            CampaignNotFoundException.class,
            ItemNotFoundException.class,
            CharacterNotFoundException.class
    })
    ResponseEntity<ErrorResponse> handleAPIObjectNotFound(final Exception e) {
        return ResponseEntity
                .status(NOT_FOUND)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handle400BadRequest(final Exception e) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(BAD_REQUEST, e.getMessage()));
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

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> handle415UnsupportedMediaType(final Exception e) {
        return ResponseEntity
                .status(UNSUPPORTED_MEDIA_TYPE)
                .contentType(APPLICATION_PROBLEM_JSON)
                .body(new ErrorResponse(UNSUPPORTED_MEDIA_TYPE, e.getMessage()));
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
