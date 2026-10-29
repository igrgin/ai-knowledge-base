package com.aiknowledgebase.shared;

import java.util.Objects;

/** A deliberate application failure, independent of Spring and HTTP. */
public final class ApplicationException extends RuntimeException {
    private final ErrorCode code;

    public ApplicationException(ErrorCode code) {
        super(Objects.requireNonNull(code).name());
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }
}
