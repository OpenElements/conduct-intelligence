package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A fallback implementation of CodeOfConductProvider that provides a default Code of Conduct.
 * This is used when no other CodeOfConductProvider implementations are available.
 */
public class FallbackCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(FallbackCodeOfConductProvider.class);
    private static final String DEFAULT_COC_RESOURCE = "DEFAULT_CODE_OF_CONDUCT.md";
    
    private final String defaultCodeOfConduct;

    public FallbackCodeOfConductProvider() {
        log.warn("Initializing fallback Code of Conduct provider. This provides a generic Code of Conduct.");
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_COC_RESOURCE);
            defaultCodeOfConduct = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read default Code of Conduct from resources", e);
            throw new IllegalStateException("Failed to load default Code of Conduct resource", e);
        }
    }

    @Override
    public boolean supports(@NonNull TextfileType type) {
        // We support all text file types with our default content
        return true;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        return defaultCodeOfConduct;
    }
}
