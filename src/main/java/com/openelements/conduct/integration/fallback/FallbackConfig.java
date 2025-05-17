package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackConfig.class);

    @Bean
    @ConditionalOnMissingBean(ConductChecker.class)
    ConductChecker fallbackConductChecker(@Autowired(required = false) List<CodeOfConductProvider> providers) {
        log.warn("No ConductChecker implementation found. Using fallback implementation with basic keyword analysis.");

        CodeOfConductProvider provider;
        if (providers == null || providers.isEmpty()) {
            log.warn("No CodeOfConductProvider found, creating a fallback provider");
            provider = new FallbackCodeOfConductProvider();
        } else {
            provider = providers.get(0);
            log.info("Using CodeOfConductProvider: {}", provider.getClass().getSimpleName());
        }
        
        return new FallbackConductChecker(provider);
    }
}
