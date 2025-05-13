package com.openelements.conduct.integration.log;

import com.openelements.conduct.data.ResultHandler;
import com.openelements.conduct.integration.discord.DiscordIntegration;
import java.util.logging.LogRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "conductIntelligence.integration.log.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class LogConfig {

    @Bean
    ResultHandler logResultHandler() {
        return new LogIntegration();
    }

}
