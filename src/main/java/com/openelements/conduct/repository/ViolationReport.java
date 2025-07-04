package com.openelements.conduct.repository;

import com.openelements.conduct.data.ViolationState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record ViolationReport(
    @NonNull String id,
    @Nullable String messageTitle,
    @NonNull String messageContent,
    @NonNull URI messageUrl,
    @NonNull ViolationState violationState,
    @NonNull String reason,
    @NonNull LocalDateTime timestamp
) {
    public ViolationReport {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(messageContent, "messageContent must not be null");
        Objects.requireNonNull(messageUrl, "messageUrl must not be null");
        Objects.requireNonNull(violationState, "violationState must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
    }

    public ViolationReport(@Nullable String messageTitle, @NonNull String messageContent, 
                          @NonNull URI messageUrl, @NonNull ViolationState violationState, 
                          @NonNull String reason) {
        this(UUID.randomUUID().toString(), messageTitle, messageContent, messageUrl, 
             violationState, reason, LocalDateTime.now());
    }
}
