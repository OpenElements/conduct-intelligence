package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

public class FallbackCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(FallbackCodeOfConductProvider.class);
    private static final String DEFAULT_COC_RESOURCE = "DEFAULT_CODE_OF_CONDUCT.md";

    @Override
    public boolean supports(@NonNull TextfileType type) {
        return true;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        log.warn("Using fallback Code of Conduct provider. This provides a generic Code of Conduct.");
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_COC_RESOURCE);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read default Code of Conduct from resources", e);
            return "# Code of Conduct\n\nThis is a placeholder Code of Conduct. The actual Code of Conduct file could not be loaded.";
        }
    }
}
