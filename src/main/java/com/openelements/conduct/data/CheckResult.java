package com.openelements.conduct.data;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public record CheckResult(@NonNull Message message, @NonNull ViolationState state, @NonNull String reason) {

    public CheckResult {
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
    }
}
