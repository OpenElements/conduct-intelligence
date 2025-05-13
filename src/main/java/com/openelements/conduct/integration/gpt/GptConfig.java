package com.openelements.conduct.integration.gpt;

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
public class GptConfig {

    @Value("${conductIntelligence.integration.gpt.apiKey}")
    private String gptApiKey;

    @Bean
    ConductChecker gptBasedConductChecker(@NonNull final CodeOfConductProvider codeOfConductProvider) {
        return new GptBasedConductChecker(gptApiKey, codeOfConductProvider);
    }
}
