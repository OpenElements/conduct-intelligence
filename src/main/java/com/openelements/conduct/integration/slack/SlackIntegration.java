package com.openelements.conduct.integration.slack;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ResultHandler;
import com.openelements.conduct.data.ViolationState;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
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

    public SlackIntegration(@NonNull String slackToken, @NonNull String channelId) {
        Objects.requireNonNull(slackToken, "slackToken must not be null");
        this.channelId = Objects.requireNonNull(channelId, "channelId must not be null");
        if (channelId.isBlank()) {
            throw new IllegalArgumentException("channelId must not be empty");
        }
        log.info("Initializing Slack integration");
        try {
            Slack slack = Slack.getInstance();
            this.slackClient = slack.methods(slackToken);
            
            // Send initialization notification
            sendInitializationMessage();
            log.info("Slack integration initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Slack integration", e);
            throw new RuntimeException("Failed to initialize Slack integration", e);
        }
    }
    
    private void sendInitializationMessage() {
    try {
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text("🚀 *Conduct Guardian* has been successfully connected to this Slack channel. " +
                      "Code of conduct violations will be reported here.")
                .build();

        var response = slackClient.chatPostMessage(request);
        if (!response.isOk()) {
            log.error("Slack API Error: {}", response.getError());  // <-- this is key
        } else {
            log.info("Message posted to Slack successfully: {}", response.getMessage().getText());
        }
    } catch (IOException | SlackApiException e) {
        log.warn("Could not send initialization message to Slack channel", e);
    }
}


    @Override
    public void handle(@NonNull CheckResult result) {
        Objects.requireNonNull(result, "result must not be null");
        
        final CompletableFuture<Void> future = new CompletableFuture<>();
        
        String emoji = "✅";
        if (result.state() == ViolationState.POSSIBLE_VIOLATION) {
            emoji = "⚠️";
        } else if (result.state() == ViolationState.VIOLATION) {
            emoji = "🚫";
        }
        
        String message = String.format("%s Check result: %s, State: %s, Reason: %s",
                emoji, result.message().link(), result.state(), result.reason());
        
        try {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channelId)
                    .text(message)
                    .build();
            
            slackClient.chatPostMessage(request);
            future.complete(null);
        } catch (IOException | SlackApiException e) {
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
