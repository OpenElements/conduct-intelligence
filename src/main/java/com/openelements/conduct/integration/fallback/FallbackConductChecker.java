package com.openelements.conduct.integration.fallback;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.ViolationState;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FallbackConductChecker implements ConductChecker {

    private static final Logger log = LoggerFactory.getLogger(FallbackConductChecker.class);

    @Override
    public @NonNull CheckResult check(@NonNull Message message) {
        log.warn("Using fallback conduct checker. No actual checking is performed.");
        return new CheckResult(
                message,
                ViolationState.NONE,
                "No conduct checking performed. This is a fallback implementation."
        );
    }
}
