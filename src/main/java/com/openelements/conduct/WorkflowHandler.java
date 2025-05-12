package com.openelements.conduct;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.ResultHandler;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowHandler {

    private final static Logger log = LoggerFactory.getLogger(WorkflowHandler.class);

    private final ConductChecker conductChecker;

    private final ResultHandler resultHandler;

    public WorkflowHandler(@NonNull final ConductChecker conductChecker, @NonNull final ResultHandler resultHandler) {
        this.conductChecker = Objects.requireNonNull(conductChecker, "ConductChecker cannot be null");
        this.resultHandler = Objects.requireNonNull(resultHandler, "ResultHandler cannot be null");
    }

    public void handleMessage(@NonNull Message message) {
        Objects.requireNonNull(message, "Message cannot be null");
        try {
            final CheckResult result = conductChecker.check(message);
            resultHandler.handle(result);
        } catch (Exception e) {
            log.error("Error processing message: " + message.link(), e);
        }
    }
}
