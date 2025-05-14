package com.openelements.conduct.integration.file;

import com.openelements.conduct.data.CodeOfConductProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "guardian.integration.coc.file.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class FileConfig {

    @Bean
    CodeOfConductProvider fileBasedCodeOfConductProvider() {
        return new FileBasedCodeOfConductProvider();
    }
}
