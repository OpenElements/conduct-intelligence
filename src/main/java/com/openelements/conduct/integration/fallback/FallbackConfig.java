package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the fallback Code of Conduct provider.
 * This will only be used if no other CodeOfConductProvider implementations are available.
 */
@Configuration
public class FallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackConfig.class);

    @Bean
    @ConditionalOnMissingBean(CodeOfConductProvider.class)
    CodeOfConductProvider fallbackCodeOfConductProvider() {
        log.warn("No CodeOfConductProvider implementation found. Using fallback implementation with a generic Code of Conduct.");
        return new FallbackCodeOfConductProvider();
    }
}
