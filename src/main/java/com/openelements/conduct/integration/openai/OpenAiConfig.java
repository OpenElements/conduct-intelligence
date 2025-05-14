package com.openelements.conduct.integration.openai;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "conductIntelligence.integration.gpt.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OpenAiConfig {

    @Value("${conductIntelligence.integration.openai.apiKey}")
    private String apiKey;

    @Value("${conductIntelligence.integration.openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${conductIntelligence.integration.openai.endpoint:https://api.openai.com/v1/chat/completions}")
    private String endpoint;

    @Bean
    ConductChecker gptBasedConductChecker(@NonNull final CodeOfConductProvider codeOfConductProvider) {
        return new OpenAiBasedConductChecker(endpoint, apiKey, model, codeOfConductProvider);
    }
}
