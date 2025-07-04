package com.openelements.conduct.service;

import com.openelements.conduct.api.dto.AnalysisDto;
import com.openelements.conduct.api.dto.TrendSummaryDto;
import com.openelements.conduct.data.ViolationState;
import com.openelements.conduct.repository.ViolationReport;
import com.openelements.conduct.repository.ViolationReportRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final ViolationReportRepository repository;

    @Autowired
    public AnalysisService(@NonNull ViolationReportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public AnalysisDto generateAnalysis() {
        List<ViolationReport> reports = repository.findAll();
        
        if (reports.isEmpty()) {
            return createEmptyAnalysis();
        }

        // Calculate total counts
        int totalNoViolationCount = (int) reports.stream()
                .filter(r -> r.violationState() == ViolationState.NONE).count();
        int totalPossibleViolationCount = (int) reports.stream()
                .filter(r -> r.violationState() == ViolationState.POSSIBLE_VIOLATION).count();
        int totalViolationCount = (int) reports.stream()
                .filter(r -> r.violationState() == ViolationState.VIOLATION).count();

        // Calculate daily averages
        Map<String, List<ViolationReport>> reportsByDay = reports.stream()
                .collect(Collectors.groupingBy(
                    report -> report.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ));

        int averageNoViolationCountPerDay = calculateDailyAverage(reportsByDay, ViolationState.NONE);
        int averagePossibleViolationCountPerDay = calculateDailyAverage(reportsByDay, ViolationState.POSSIBLE_VIOLATION);
        int averageViolationCountPerDay = calculateDailyAverage(reportsByDay, ViolationState.VIOLATION);

        // Calculate daily maximums
        int maxNoViolationCountPerDay = calculateDailyMaximum(reportsByDay, ViolationState.NONE);
        int maxPossibleViolationCountPerDay = calculateDailyMaximum(reportsByDay, ViolationState.POSSIBLE_VIOLATION);
        int maxViolationCountPerDay = calculateDailyMaximum(reportsByDay, ViolationState.VIOLATION);

        // Calculate this week averages
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        List<ViolationReport> thisWeekReports = reports.stream()
                .filter(r -> r.timestamp().isAfter(weekStart))
                .collect(Collectors.toList());

        Map<String, List<ViolationReport>> thisWeekReportsByDay = thisWeekReports.stream()
                .collect(Collectors.groupingBy(
                    report -> report.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ));

        int averageNoViolationCountPerDayInThisWeek = calculateDailyAverage(thisWeekReportsByDay, ViolationState.NONE);
        int averagePossibleViolationCountPerDayInThisWeek = calculateDailyAverage(thisWeekReportsByDay, ViolationState.POSSIBLE_VIOLATION);
        int averageViolationCountPerDayInThisWeek = calculateDailyAverage(thisWeekReportsByDay, ViolationState.VIOLATION);

        // Calculate growth metrics
        LocalDateTime lastWeekStart = LocalDateTime.now().minusDays(14);
        LocalDateTime lastWeekEnd = LocalDateTime.now().minusDays(7);
        
        List<ViolationReport> lastWeekReports = reports.stream()
                .filter(r -> r.timestamp().isAfter(lastWeekStart) && r.timestamp().isBefore(lastWeekEnd))
                .collect(Collectors.toList());

        double generalGrowthOfChecksInPercentage = calculateGrowthPercentage(lastWeekReports.size(), thisWeekReports.size());
        double growthOfNoViolationCountAgainstLastWeek = calculateGrowthPercentage(
                (int) lastWeekReports.stream().filter(r -> r.violationState() == ViolationState.NONE).count(),
                (int) thisWeekReports.stream().filter(r -> r.violationState() == ViolationState.NONE).count()
        );
        double growthOfPossibleViolationCountAgainstLastWeek = calculateGrowthPercentage(
                (int) lastWeekReports.stream().filter(r -> r.violationState() == ViolationState.POSSIBLE_VIOLATION).count(),
                (int) thisWeekReports.stream().filter(r -> r.violationState() == ViolationState.POSSIBLE_VIOLATION).count()
        );
        double growthOfViolationCountAgainstLastWeek = calculateGrowthPercentage(
                (int) lastWeekReports.stream().filter(r -> r.violationState() == ViolationState.VIOLATION).count(),
                (int) thisWeekReports.stream().filter(r -> r.violationState() == ViolationState.VIOLATION).count()
        );

        return new AnalysisDto(
            totalNoViolationCount,
            totalPossibleViolationCount,
            totalViolationCount,
            averageNoViolationCountPerDay,
            averagePossibleViolationCountPerDay,
            averageViolationCountPerDay,
            maxNoViolationCountPerDay,
            maxPossibleViolationCountPerDay,
            maxViolationCountPerDay,
            averageNoViolationCountPerDayInThisWeek,
            averagePossibleViolationCountPerDayInThisWeek,
            averageViolationCountPerDayInThisWeek,
            generalGrowthOfChecksInPercentage,
            growthOfNoViolationCountAgainstLastWeek,
            growthOfPossibleViolationCountAgainstLastWeek,
            growthOfViolationCountAgainstLastWeek,
            LocalDateTime.now()
        );
    }

    public TrendSummaryDto generateTrendSummary() {
        AnalysisDto analysis = generateAnalysis();
        
        String trend = determineTrend(analysis.generalGrowthOfChecksInPercentage());
        String description = String.format("General growth: %.1f%%, Violations growth: %.1f%%", 
            analysis.generalGrowthOfChecksInPercentage(), 
            analysis.growthOfViolationCountAgainstLastWeek());
        
        return new TrendSummaryDto(
            trend,
            analysis.generalGrowthOfChecksInPercentage(),
            description,
            analysis.totalNoViolationCount() + analysis.totalPossibleViolationCount() + analysis.totalViolationCount(),
            analysis.averageViolationCountPerDay()
        );
    }

    private AnalysisDto createEmptyAnalysis() {
        return new AnalysisDto(
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, LocalDateTime.now()
        );
    }

    private int calculateDailyAverage(Map<String, List<ViolationReport>> reportsByDay, ViolationState state) {
        if (reportsByDay.isEmpty()) return 0;
        
        double average = reportsByDay.values().stream()
                .mapToInt(dayReports -> (int) dayReports.stream()
                        .filter(r -> r.violationState() == state)
                        .count())
                .average()
                .orElse(0.0);
        
        return (int) Math.round(average);
    }

    private int calculateDailyMaximum(Map<String, List<ViolationReport>> reportsByDay, ViolationState state) {
        return reportsByDay.values().stream()
                .mapToInt(dayReports -> (int) dayReports.stream()
                        .filter(r -> r.violationState() == state)
                        .count())
                .max()
                .orElse(0);
    }

    private double calculateGrowthPercentage(int oldValue, int newValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100.0 : 0.0;
        }
        return ((double) (newValue - oldValue) / oldValue) * 100.0;
    }

    private String determineTrend(double growthPercentage) {
        if (Math.abs(growthPercentage) < 10) {
            return "STABLE";
        } else if (growthPercentage > 0) {
            return "INCREASING";
        } else {
            return "DECREASING";
        }
    }
}
