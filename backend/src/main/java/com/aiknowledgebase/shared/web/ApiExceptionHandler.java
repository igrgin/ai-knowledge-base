package com.aiknowledgebase.shared.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        return new ResponseEntity<>(error(status, servletRequest), headers, status);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> unexpected(Exception exception, HttpServletRequest request) {
        // Exception messages and stack traces may contain SQL, document text or credentials.
        LOG.error("Request {} failed with {}", RequestIdFilter.requestId(request), exception.getClass().getName());
        return ResponseEntity.internalServerError().body(error(HttpStatus.INTERNAL_SERVER_ERROR, request));
    }

    private ApiError error(HttpStatusCode status, HttpServletRequest request) {
        HttpStatus knownStatus = HttpStatus.resolve(status.value());
        String code = switch (status.value()) {
            case 400 -> "BAD_REQUEST";
            case 401 -> "UNAUTHENTICATED";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 500 -> "INTERNAL_ERROR";
            default -> "HTTP_" + status.value();
        };
        String message = knownStatus == null ? "Request failed" : knownStatus.getReasonPhrase();
        return new ApiError(code, message, RequestIdFilter.requestId(request));
    }
}
