package com.openelements.conduct.integration.openai;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(
        name = "guardian.integration.openai.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OpenAiConfig {

    @Value("${guardian.integration.openai.apiKey}")
    private String apiKey;

    @Value("${guardian.integration.openai.model}")
    private String model;

    @Value("${guardian.integration.openai.endpoint}")
    private String endpoint;

    @Bean
    @Primary
    ConductChecker gptBasedConductChecker(@NonNull final CodeOfConductProvider codeOfConductProvider) {
        return new OpenAiBasedConductChecker(endpoint, apiKey, model, codeOfConductProvider);
    }
}
