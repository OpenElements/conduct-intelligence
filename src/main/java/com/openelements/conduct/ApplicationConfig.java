package com.openelements.conduct;

import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.ResultHandler;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    WorkflowHandler workflowHandler(@NonNull final ConductChecker conductChecker, @NonNull final List<ResultHandler> resultHandlers) {
        return new WorkflowHandler(conductChecker, resultHandlers);
    }

}
