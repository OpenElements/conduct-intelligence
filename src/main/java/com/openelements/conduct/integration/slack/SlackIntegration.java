package com.openelements.conduct.integration.slack;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ResultHandler;
import com.openelements.conduct.data.ViolationState;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackIntegration implements ResultHandler {

    private final static Logger log = LoggerFactory.getLogger(SlackIntegration.class);

    private final MethodsClient slackClient;
    private final String channelId;

    public SlackIntegration(@NonNull final String slackToken, @NonNull final String channelId) {
        Objects.requireNonNull(slackToken, "slackToken must not be null");
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
        if (channelId.isBlank()) {
            throw new IllegalArgumentException("channelId must not be empty");
        }
        log.info("Initializing Slack integration");
        try {
            final Slack slack = Slack.getInstance();
            this.slackClient = slack.methods(slackToken);

            sendInitializationMessage();
            log.info("Slack integration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Slack integration", e);
            throw new RuntimeException("Failed to initialize Slack integration", e);
        }
    }
    
    private void sendInitializationMessage() {
        try {
            final ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text("🚀 *Conduct Guardian* has been successfully connected to this Slack channel. " +
                          "Code of conduct violations will be reported here.")
                    .build();
            
            final ChatPostMessageResponse response = slackClient.chatPostMessage(request);
            if (!response.isOk()) {
                log.error("Slack API Error: {}", response.getError());
                throw new RuntimeException("Failed to send initialization message to Slack: " + response.getError());
            } else {
                log.info("Message posted to Slack successfully: {}", response.getMessage().getText());
            }
        } catch (Exception e) {
            log.error("Could not send initialization message to Slack channel", e);
            throw new RuntimeException("Failed to send initialization message to Slack", e);
        }
    }

    @Override
    public void handle(@NonNull CheckResult result) {
        Objects.requireNonNull(result, "result must not be null");

        if (result.state() == ViolationState.NONE) {
            log.debug("No violation found, not sending message to Slack");
            return;
        }
        
        final CompletableFuture<Void> future = new CompletableFuture<>();
        
        String emoji = "⚠️";
        if (result.state() == ViolationState.VIOLATION) {
            emoji = "🚫";
        }
        
        String message = String.format("%s Check result: \n" +
                "Link: %s\n" +
                "State: %s\n" +
                "Reason: %s",
                emoji, result.message().link(), result.state(), result.reason());
        
        try {
            final ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text(message)
                    .build();
            
            final ChatPostMessageResponse response = slackClient.chatPostMessage(request);
            if (!response.isOk()) {
                log.error("Slack API Error: {}", response.getError());
                future.completeExceptionally(new RuntimeException("Slack API Error: " + response.getError()));
            } else {
                future.complete(null);
            }
        } catch (Exception e) {
            log.error("Error sending message to Slack", e);
            future.completeExceptionally(e);
        }
        
        try {
            future.get(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Slack channel", e);
        }
    }
}
