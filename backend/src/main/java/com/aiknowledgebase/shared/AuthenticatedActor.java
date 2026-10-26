package com.aiknowledgebase.shared;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

/** Account identity established by server-side authentication, never by request data. */
public record AuthenticatedActor(UUID accountId) implements Principal, Serializable {
    public AuthenticatedActor {
        Objects.requireNonNull(accountId, "accountId");
    }

    @Override
    public String getName() {
        return accountId.toString();
    }
}
