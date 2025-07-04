package com.openelements.conduct.controller;

import com.openelements.conduct.api.dto.AnalysisDto;
import com.openelements.conduct.service.AnalysisService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    @Autowired
    public AnalysisController(@NonNull AnalysisService analysisService) {
        this.analysisService = Objects.requireNonNull(analysisService, "analysisService must not be null");
    }

    @GetMapping
    public AnalysisDto getAnalysis() {
        return analysisService.generateAnalysis();
    }
}
