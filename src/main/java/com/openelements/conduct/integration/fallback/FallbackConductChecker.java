package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.TextfileType;
import com.openelements.conduct.data.ViolationState;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A fallback implementation of ConductChecker that uses a simple keyword-based algorithm
 * to detect potential code of conduct violations when no other checker is available.
 */
public class FallbackConductChecker implements ConductChecker {

    private static final Logger log = LoggerFactory.getLogger(FallbackConductChecker.class);
    
    private final CodeOfConductProvider codeOfConductProvider;
    
    // Common offensive terms and patterns that might indicate CoC violations
    private static final Set<String> OFFENSIVE_TERMS = new HashSet<>(Arrays.asList(
            "idiot", "stupid", "dumb", "moron", "retard", 
            "crap", "shit", "fuck", "damn", "ass", "asshole",
            "bitch", "bastard", "dick", "cunt", "whore", "slut",
            "hate", "kill", "die", "attack", "terrible", "horrible",
            "useless", "worthless", "incompetent", "pathetic"
    ));
    
    // Terms that might indicate harassment or discrimination
    private static final Set<String> DISCRIMINATORY_TERMS = new HashSet<>(Arrays.asList(
            "racist", "sexist", "homophobic", "transphobic", "bigot",
            "nazi", "fascist", "communist", "terrorist",
            "gay", "lesbian", "trans", "queer", "black", "white", "asian", "latino", "hispanic",
            "jew", "muslim", "christian", "hindu", "buddhist",
            "woman", "man", "girl", "boy", "female", "male"
    ));
    
    // Patterns that might indicate personal attacks
    private static final List<Pattern> ATTACK_PATTERNS = Arrays.asList(
            Pattern.compile("(?i)you are (a|an) ([a-z\\s]+)"),
            Pattern.compile("(?i)you're (a|an) ([a-z\\s]+)"),
            Pattern.compile("(?i)you ([a-z]+) (like|as) (a|an) ([a-z\\s]+)"),
            Pattern.compile("(?i)your ([a-z\\s]+) is ([a-z\\s]+)"),
            Pattern.compile("(?i)go ([a-z]+) yourself")
    );

    public FallbackConductChecker(@NonNull CodeOfConductProvider codeOfConductProvider) {
        this.codeOfConductProvider = Objects.requireNonNull(codeOfConductProvider, 
                "codeOfConductProvider must not be null");
        log.warn("Using fallback conduct checker with basic keyword analysis. This is less accurate than AI-based checkers.");
    }

    @Override
    public @NonNull CheckResult check(@NonNull Message message) {
        Objects.requireNonNull(message, "message must not be null");
        
        log.info("Performing basic keyword analysis on message: {}", message.link());
        
        String messageContent = message.message().toLowerCase();
        String messageTitle = message.title() != null ? message.title().toLowerCase() : "";
        
        // Combine title and message for analysis
        String fullContent = messageTitle + " " + messageContent;
        
        // Check for offensive terms
        List<String> foundOffensiveTerms = new ArrayList<>();
        for (String term : OFFENSIVE_TERMS) {
            if (containsWholeWord(fullContent, term)) {
                foundOffensiveTerms.add(term);
            }
        }

        List<String> foundDiscriminatoryContexts = new ArrayList<>();
        for (String term : DISCRIMINATORY_TERMS) {
            if (containsWholeWord(fullContent, term)) {

                if (isInNegativeContext(fullContent, term)) {
                    foundDiscriminatoryContexts.add(term);
                }
            }
        }

        boolean containsAttackPattern = ATTACK_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(fullContent).find());

        if (!foundOffensiveTerms.isEmpty() || !foundDiscriminatoryContexts.isEmpty() || containsAttackPattern) {
            StringBuilder reasonBuilder = new StringBuilder("Potential code of conduct violation detected: ");
            
            if (!foundOffensiveTerms.isEmpty()) {
                reasonBuilder.append("Found offensive terms: ")
                        .append(String.join(", ", foundOffensiveTerms))
                        .append(". ");
            }
            
            if (!foundDiscriminatoryContexts.isEmpty()) {
                reasonBuilder.append("Found potentially discriminatory language around: ")
                        .append(String.join(", ", foundDiscriminatoryContexts))
                        .append(". ");
            }
            
            if (containsAttackPattern) {
                reasonBuilder.append("Detected language patterns that may constitute personal attacks. ");
            }
            
            reasonBuilder.append("Please review the message against the code of conduct.");

            ViolationState state = (foundOffensiveTerms.size() > 2 || !foundDiscriminatoryContexts.isEmpty()) 
                    ? ViolationState.VIOLATION 
                    : ViolationState.POSSIBLE_VIOLATION;
            
            return new CheckResult(message, state, reasonBuilder.toString());
        }
        
        return new CheckResult(
                message,
                ViolationState.NONE,
                "No potential violations detected by basic keyword analysis."
        );
    }
    
    /**
     * Checks if the text contains the whole word (not just as part of another word)
     */
    private boolean containsWholeWord(String text, String word) {
        String pattern = "\\b" + Pattern.quote(word) + "\\b";
        return Pattern.compile(pattern).matcher(text).find();
    }

    private boolean isInNegativeContext(String text, String term) {
        // Look for the term in proximity to negative words
        int termIndex = text.indexOf(term);
        if (termIndex == -1) return false;
        
        // Get a window of text around the term
        int startIndex = Math.max(0, termIndex - 30);
        int endIndex = Math.min(text.length(), termIndex + term.length() + 30);
        String context = text.substring(startIndex, endIndex);
        
        // Check for negative words in this context
        for (String negativeWord : Arrays.asList("hate", "stupid", "bad", "terrible", "awful", "worst", 
                "not", "never", "against", "dislike", "reject", "wrong", "evil", "sick", "disgusting")) {
            if (containsWholeWord(context, negativeWord)) {
                return true;
            }
        }
        
        return false;
    }
}
