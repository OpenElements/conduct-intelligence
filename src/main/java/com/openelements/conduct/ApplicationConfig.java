package com.openelements.conduct;

import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.ResultHandler;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    WorkflowHandler workflowHandler(@NonNull final ConductChecker conductChecker, @NonNull final ResultHandler resultHandler) {
        return new WorkflowHandler(conductChecker, resultHandler);
    }

}
