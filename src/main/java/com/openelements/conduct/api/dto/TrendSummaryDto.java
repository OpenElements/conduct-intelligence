package com.openelements.conduct.api.dto;

import org.jspecify.annotations.NonNull;

public record TrendSummaryDto(
    @NonNull String trend,
    double changePercentage,
    @NonNull String description,
    long totalReports,
    double averageViolationsPerDay
) {}
