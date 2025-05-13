package com.openelements.conduct.integration.log;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ResultHandler;
import com.openelements.conduct.data.ViolationState;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogIntegration implements ResultHandler {

    private final static Logger log = LoggerFactory.getLogger(LogIntegration.class);

    @Override
    public void handle(@NonNull CheckResult result) {
        Objects.requireNonNull(result, "result must not be null");
        if(result.state() == ViolationState.NONE) {
            log.info("No Violation found :) -> {}", result.message().link());
        }
        if(result.state() == ViolationState.POSSIBLE_VIOLATION) {
            log.warn("Possible Violation found :( -> {} \n {}", result.message().link(), result.reason());
        }
        if(result.state() == ViolationState.VIOLATION) {
            log.warn("Violation found!!!! :( -> {} \n {}", result.message().link(), result.reason());
        }
    }
}
