package com.aiknowledgebase.shared.web;

import java.util.UUID;

/** Public errors contain only a stable code, safe message and server correlation ID. */
public record ApiError(String code, String message, UUID requestId) {
}
