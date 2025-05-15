package com.openelements.conduct;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.ResultHandler;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHandler {

    private final static Logger log = LoggerFactory.getLogger(WorkflowHandler.class);

    private final ConductChecker conductChecker;

    private final List<ResultHandler> resultHandlers;

    public WorkflowHandler(@NonNull final ConductChecker conductChecker, @NonNull final List<ResultHandler> resultHandlers) {
        this.conductChecker = Objects.requireNonNull(conductChecker, "ConductChecker cannot be null");
        this.resultHandlers = Objects.requireNonNull(resultHandlers, "ResultHandlers cannot be null");
        if (resultHandlers.isEmpty()) {
            log.warn("No ResultHandlers configured. Results will not be processed.");
        }
    }

    public void handleMessage(@NonNull Message message) {
        Objects.requireNonNull(message, "Message cannot be null");
        try {
            final CheckResult result = conductChecker.check(message);
            for (ResultHandler handler : resultHandlers) {
                try {
                    handler.handle(result);
                } catch (Exception e) {
                    log.error("Error in ResultHandler: " + handler.getClass().getSimpleName(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: " + message.link(), e);
        }
    }
}
