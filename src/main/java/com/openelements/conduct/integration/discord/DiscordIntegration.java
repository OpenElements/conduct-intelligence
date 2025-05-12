package com.openelements.conduct.integration.discord;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ResultHandler;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordIntegration implements ResultHandler {

    private final static Logger log = LoggerFactory.getLogger(DiscordIntegration.class);

    private final JDA jda;

    private final String channelId;

    public DiscordIntegration(@NonNull String discordToken, @NonNull String channelId) {
        Objects.requireNonNull(discordToken, "discordToken must not be null");
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
        if (channelId.isBlank()) {
            throw new IllegalArgumentException("channelId must not be empty");
        }
        log.info("Initializing Discord bot");
        try {
            jda = JDABuilder.createDefault(discordToken).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Discord bot", e);
        }
    }

    @Override
    public void handle(@NonNull CheckResult result) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        final TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            String message = String.format("Check result: %s, State: %s, Reason: %s",
                    result.message(), result.state(), result.reason());
            channel.sendMessage(message).queue(m -> future.complete(null),
                    throwable -> future.completeExceptionally(throwable));
        } else {
            throw new IllegalArgumentException("Channel not found: " + channelId);
        }
        try {
            future.get(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Discord channel", e);
        }
    }
}