package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.ViolationState;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "guardian.integration.openai.enabled",
        havingValue = "false",
        matchIfMissing = false
)
public class FallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackConfig.class);

    @Bean
    ConductChecker fallbackConductChecker() {
        log.warn("=======================================================================");
        log.warn("WARNING: Using fallback conduct checker. NO ACTUAL CHECKING WILL BE PERFORMED.");
        log.warn("This is only for development/testing. For production use, enable OpenAI.");
        log.warn("To enable OpenAI, set guardian.integration.openai.enabled=true and configure your API key.");
        log.warn("=======================================================================");
        
        return new ConductChecker() {
            @Override
            public @NonNull CheckResult check(@NonNull Message message) {
                log.warn("Conduct check requested but OpenAI is not configured. No actual checking performed for: {}", message.link());
                return new CheckResult(
                    message,
                    ViolationState.NONE,
                    "No checking performed. OpenAI integration is disabled. This is a placeholder result."
                );
            }
        };
    }
}
