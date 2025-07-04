package com.openelements.conduct.api.dto;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

public record AnalysisDto(
    // Total counts
    int totalNoViolationCount,
    int totalPossibleViolationCount,
    int totalViolationCount,
    
    // Daily averages
    int averageNoViolationCountPerDay,
    int averagePossibleViolationCountPerDay,
    int averageViolationCountPerDay,
    
    // Daily maximums
    int maxNoViolationCountPerDay,
    int maxPossibleViolationCountPerDay,
    int maxViolationCountPerDay,
    
    // This week averages
    int averageNoViolationCountPerDayInThisWeek,
    int averagePossibleViolationCountPerDayInThisWeek,
    int averageViolationCountPerDayInThisWeek,
    
    // Growth metrics
    double generalGrowthOfChecksInPercentage,
    double growthOfNoViolationCountAgainstLastWeek,
    double growthOfPossibleViolationCountAgainstLastWeek,
    double growthOfViolationCountAgainstLastWeek,
    
    @NonNull LocalDateTime analysisTimestamp
) {}
