package com.openelements.conduct.integration.composite;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.integration.fallback.FallbackCodeOfConductProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the composite Code of Conduct provider.
 * This combines multiple providers with fallback behavior.
 */
@Configuration
@ConditionalOnProperty(
        name = "guardian.integration.composite.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CompositeConfig {

    @Bean
    @Primary
    CodeOfConductProvider compositeCodeOfConductProvider(@Autowired(required = false) List<CodeOfConductProvider> providers) {
        List<CodeOfConductProvider> allProviders = new ArrayList<>();
        
        // Add all available providers
        if (providers != null && !providers.isEmpty()) {
            // Filter out the fallback provider if there are other providers
            providers.stream()
                    .filter(p -> !(p instanceof FallbackCodeOfConductProvider))
                    .forEach(allProviders::add);
        }
        
        // Always add a fallback provider as the last resort
        allProviders.add(new FallbackCodeOfConductProvider());
        
        return new CompositeCodeOfConductProvider(allProviders);
    }
}
