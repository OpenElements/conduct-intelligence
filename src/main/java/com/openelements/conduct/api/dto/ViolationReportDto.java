package com.openelements.conduct.api.dto;

import com.openelements.conduct.data.ViolationState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.time.LocalDateTime;

public record ViolationReportDto(
    @NonNull String id,
    @Nullable String messageTitle,
    @NonNull String messageContent,
    @NonNull URI linkToViolation,
    @NonNull ViolationState violationState,
    @NonNull String reason,
    @NonNull LocalDateTime timestamp
) {}
