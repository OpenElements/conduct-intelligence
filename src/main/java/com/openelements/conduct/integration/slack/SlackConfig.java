package com.openelements.conduct.integration.slack;

import com.openelements.conduct.data.ResultHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "conductIntelligence.integration.slack.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class SlackConfig {

    @Value("${conductIntelligence.integration.slack.token}")
    private String slackToken;

    @Value("${conductIntelligence.integration.slack.channelId}")
    private String slackChannelId;

    @Bean
    ResultHandler slackResultHandler() {
        return new SlackIntegration(slackToken, slackChannelId);
    }
}
