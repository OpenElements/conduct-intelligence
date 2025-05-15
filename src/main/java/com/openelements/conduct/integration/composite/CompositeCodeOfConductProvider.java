package com.openelements.conduct.integration.composite;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * A composite implementation of CodeOfConductProvider that tries multiple providers in order.
 * This allows for fallback behavior if the primary provider fails.
 */
public class CompositeCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(CompositeCodeOfConductProvider.class);

    private final List<CodeOfConductProvider> providers;

    public CompositeCodeOfConductProvider(@NonNull List<CodeOfConductProvider> providers) {
        this.providers = Objects.requireNonNull(providers, "providers must not be null");
        if (providers.isEmpty()) {
            throw new IllegalArgumentException("At least one provider must be specified");
        }
        log.info("Initialized composite Code of Conduct provider with {} providers", providers.size());
    }

    @Override
    public boolean supports(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");
        
        // Check if any provider supports this type
        for (CodeOfConductProvider provider : providers) {
            try {
                if (provider.supports(type)) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("Provider {} failed to check support for type {}: {}", 
                        provider.getClass().getSimpleName(), type, e.getMessage());
            }
        }
        
        return false;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");
        
        Exception lastException = null;
        
        // Try each provider in order
        for (CodeOfConductProvider provider : providers) {
            try {
                if (provider.supports(type)) {
                    String codeOfConduct = provider.getCodeOfConduct(type);
                    log.debug("Successfully retrieved Code of Conduct from provider: {}", 
                            provider.getClass().getSimpleName());
                    return codeOfConduct;
                }
            } catch (Exception e) {
                log.warn("Provider {} failed to get Code of Conduct for type {}: {}", 
                        provider.getClass().getSimpleName(), type, e.getMessage());
                lastException = e;
            }
        }
        
        // If we get here, no provider could provide the Code of Conduct
        if (lastException != null) {
            throw new RuntimeException("All Code of Conduct providers failed", lastException);
        } else {
            throw new IllegalStateException("No Code of Conduct provider supports type: " + type);
        }
    }
}
