package com.openelements.conduct.integration.slack;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ResultHandler;
import org.jspecify.annotations.NonNull;

public class SlackIntegration implements ResultHandler {

    @Override
    public void handle(@NonNull CheckResult result) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
