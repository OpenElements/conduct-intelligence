package com.openelements.conduct.integration.openai;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "guardian.integration.openai.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class OpenAiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenAiConfig.class);

    @Value("${guardian.integration.openai.apiKey}")
    private String apiKey;

    @Value("${guardian.integration.openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${guardian.integration.openai.endpoint:https://api.openai.com/v1/chat/completions}")
    private String endpoint;

    @Bean
    ConductChecker openAiBasedConductChecker(@NonNull final CodeOfConductProvider codeOfConductProvider) {
        log.info("Initializing OpenAI-based conduct checker with model: {}", model);
        return new OpenAiBasedConductChecker(endpoint, apiKey, model, codeOfConductProvider);
    }
}
