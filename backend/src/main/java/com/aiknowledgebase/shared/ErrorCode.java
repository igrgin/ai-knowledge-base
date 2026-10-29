package com.aiknowledgebase.shared;

/** Stable application-defined failures; transport status belongs to the web adapter. */
public enum ErrorCode {
    INVALID_REQUEST("The request contains missing or invalid values. Check the required fields and value formats."),
    REQUEST_BODY_UNREADABLE("The request body could not be read. Send a valid body in the documented format."),
    RESOURCE_NOT_FOUND("The requested resource is unavailable. Check the address or return to your resource list."),
    METHOD_NOT_ALLOWED("This request method is not supported. Use a method listed in the Allow header."),
    UNSUPPORTED_MEDIA_TYPE("The request content type is not supported. Use a documented Content-Type."),
    RESPONSE_TYPE_NOT_ACCEPTABLE("The requested response format is not supported. Use a documented Accept type."),
    REQUEST_TOO_LARGE("The request exceeds the allowed size. Reduce its size and try again."),
    REQUEST_TIMED_OUT("The server could not finish the request in time. Check its outcome before retrying."),
    FRAMEWORK_REQUEST_REJECTED("The server could not complete this request. Check the API requirements and response status before retrying."),
    INTERNAL_ERROR("The server could not complete the request. Try again later; if it persists, contact support with the request ID.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
