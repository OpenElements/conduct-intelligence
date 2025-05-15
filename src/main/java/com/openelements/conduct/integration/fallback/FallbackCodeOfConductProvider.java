package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fallback implementation of CodeOfConductProvider that provides a simple default Code of Conduct.
 * This is used when no other CodeOfConductProvider implementations are available.
 */
public class FallbackCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(FallbackCodeOfConductProvider.class);

    private static final String DEFAULT_CODE_OF_CONDUCT = """
            # Code of Conduct
            
            ## Our Pledge
            
            We as members, contributors, and leaders pledge to make participation in our
            community a harassment-free experience for everyone, regardless of age, body
            size, visible or invisible disability, ethnicity, sex characteristics, gender
            identity and expression, level of experience, education, socio-economic status,
            nationality, personal appearance, race, caste, color, religion, or sexual
            identity and orientation.
            
            We pledge to act and interact in ways that contribute to an open, welcoming,
            diverse, inclusive, and healthy community.
            
            ## Our Standards
            
            Examples of behavior that contributes to a positive environment for our
            community include:
            
            * Demonstrating empathy and kindness toward other people
            * Being respectful of differing opinions, viewpoints, and experiences
            * Giving and gracefully accepting constructive feedback
            * Accepting responsibility and apologizing to those affected by our mistakes,
              and learning from the experience
            * Focusing on what is best not just for us as individuals, but for the overall
              community
            
            Examples of unacceptable behavior include:
            
            * The use of sexualized language or imagery, and sexual attention or advances of
              any kind
            * Trolling, insulting or derogatory comments, and personal or political attacks
            * Public or private harassment
            * Publishing others' private information, such as a physical or email address,
              without their explicit permission
            * Other conduct which could reasonably be considered inappropriate in a
              professional setting
            
            ## Enforcement Responsibilities
            
            Community leaders are responsible for clarifying and enforcing our standards of
            acceptable behavior and will take appropriate and fair corrective action in
            response to any behavior that they deem inappropriate, threatening, offensive,
            or harmful.
            """;

    @Override
    public boolean supports(@NonNull TextfileType type) {
        // We support all text file types with our default content
        return true;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        log.warn("Using fallback Code of Conduct provider. This provides a generic Code of Conduct.");
        return DEFAULT_CODE_OF_CONDUCT;
    }
}
