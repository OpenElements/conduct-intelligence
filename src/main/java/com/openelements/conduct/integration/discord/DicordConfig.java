package com.openelements.conduct.integration.discord;

import com.openelements.conduct.data.ResultHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = "conductIntelligence.integration.discord.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DicordConfig {

    @Value("${conductIntelligence.integration.discord.token}")
    private String discordToken;

    @Value("${conductIntelligence.integration.discord.channelId}")
    private String discordChannelId;

    @Bean
    ResultHandler discordResultHandler() {
        return new DiscordIntegration(discordToken, discordChannelId);
    }

}
