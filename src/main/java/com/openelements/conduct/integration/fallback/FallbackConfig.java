package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.ConductChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the fallback conduct checker.
 * This will only be used if no other ConductChecker implementations are available.
 */
@Configuration
public class FallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackConfig.class);

    @Bean
    @ConditionalOnMissingBean(ConductChecker.class)
    ConductChecker fallbackConductChecker() {
        log.warn("No ConductChecker implementation found. Using fallback implementation that performs no actual checking.");
        return new FallbackConductChecker();
    }
}
