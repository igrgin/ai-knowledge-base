package com.aiknowledgebase.shared.web;

import com.aiknowledgebase.shared.ApplicationException;
import com.aiknowledgebase.shared.ErrorCode;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    @Nullable ResponseEntity<Object> application(ApplicationException exception, ServletWebRequest request) {
        return respond(exception.code(), new HttpHeaders(), status(exception.code()), request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body,
            HttpHeaders headers, HttpStatusCode frameworkStatus, WebRequest request) {
        ErrorCode code = frameworkCode(exception);
        // Uncatalogued framework failures keep their protocol semantics, never their detail text.
        HttpStatusCode responseStatus = code == ErrorCode.FRAMEWORK_REQUEST_REJECTED
                ? frameworkStatus : status(code);
        return respond(code, headers, responseStatus, (ServletWebRequest) request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        // The superclass warning includes the exception's message (and submitted method).
        return handleExceptionInternal(exception, null, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    @Nullable ResponseEntity<Object> unexpected(Exception exception, ServletWebRequest request) {
        if (exception instanceof ErrorResponse frameworkError) {
            return handleExceptionInternal(exception, null, frameworkError.getHeaders(),
                    frameworkError.getStatusCode(), request);
        }
        // Exception messages and stack traces may contain SQL, document text or credentials.
        LOG.error("Request {} failed with {}", RequestIdFilter.requestId(request.getRequest()),
                exception.getClass().getName());
        return respond(ErrorCode.INTERNAL_ERROR, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private @Nullable ResponseEntity<Object> respond(ErrorCode code, HttpHeaders headers,
            HttpStatusCode status, ServletWebRequest request) {
        if (request.getResponse() != null && request.getResponse().isCommitted()) {
            return null;
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(headers);
        // Errors have one explicit representation, even when Accept excludes JSON.
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(new ApiError(code.name(), code.message(),
                RequestIdFilter.requestId(request.getRequest())), responseHeaders, status);
    }

    private ErrorCode frameworkCode(Exception exception) {
        return switch (exception) {
            case HttpRequestMethodNotSupportedException ignored -> ErrorCode.METHOD_NOT_ALLOWED;
            case HttpMediaTypeNotSupportedException ignored -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            case HttpMediaTypeNotAcceptableException ignored -> ErrorCode.RESPONSE_TYPE_NOT_ACCEPTABLE;
            case NoHandlerFoundException ignored -> ErrorCode.RESOURCE_NOT_FOUND;
            case NoResourceFoundException ignored -> ErrorCode.RESOURCE_NOT_FOUND;
            case HttpMessageNotReadableException ignored -> ErrorCode.REQUEST_BODY_UNREADABLE;
            case MaxUploadSizeExceededException ignored -> ErrorCode.REQUEST_TOO_LARGE;
            case AsyncRequestTimeoutException ignored -> ErrorCode.REQUEST_TIMED_OUT;
            // These subclasses must precede their request-binding/conversion parents.
            case ConversionNotSupportedException ignored -> ErrorCode.INTERNAL_ERROR;
            case MissingPathVariableException ex -> ex.isMissingAfterConversion()
                    ? ErrorCode.INVALID_REQUEST : ErrorCode.INTERNAL_ERROR;
            case HandlerMethodValidationException ex -> ex.isForReturnValue()
                    ? ErrorCode.INTERNAL_ERROR : ErrorCode.INVALID_REQUEST;
            case TypeMismatchException ignored -> ErrorCode.INVALID_REQUEST;
            case ServletRequestBindingException ignored -> ErrorCode.INVALID_REQUEST;
            case MethodArgumentNotValidException ignored -> ErrorCode.INVALID_REQUEST;
            case MissingServletRequestPartException ignored -> ErrorCode.INVALID_REQUEST;
            case HttpMessageNotWritableException ignored -> ErrorCode.INTERNAL_ERROR;
            case MethodValidationException ignored -> ErrorCode.INTERNAL_ERROR;
            default -> ErrorCode.FRAMEWORK_REQUEST_REJECTED;
        };
    }

    private HttpStatus status(ErrorCode code) {
        return switch (code) {
            case INVALID_REQUEST -> HttpStatus.BAD_REQUEST;
            case REQUEST_BODY_UNREADABLE -> HttpStatus.BAD_REQUEST;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case METHOD_NOT_ALLOWED -> HttpStatus.METHOD_NOT_ALLOWED;
            case UNSUPPORTED_MEDIA_TYPE -> HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            case RESPONSE_TYPE_NOT_ACCEPTABLE -> HttpStatus.NOT_ACCEPTABLE;
            case REQUEST_TOO_LARGE -> HttpStatus.PAYLOAD_TOO_LARGE;
            case REQUEST_TIMED_OUT -> HttpStatus.SERVICE_UNAVAILABLE;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            // Direct application use has a fixed mapping; framework fallback retains its own status.
            case FRAMEWORK_REQUEST_REJECTED -> HttpStatus.BAD_REQUEST;
        };
    }
}
