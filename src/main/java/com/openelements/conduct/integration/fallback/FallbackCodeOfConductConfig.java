package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackCodeOfConductConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackCodeOfConductConfig.class);

    @Bean
    @ConditionalOnMissingBean(CodeOfConductProvider.class)
    CodeOfConductProvider fallbackCodeOfConductProvider() {
        log.warn("No CodeOfConductProvider implementation found. Using fallback implementation with a generic Code of Conduct.");
        return new FallbackCodeOfConductProvider();
    }
}
