package com.openelements.conduct.integration.gpt;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class GptBasedConductChecker implements ConductChecker {

    private final RestClient restClient;
    private final String endpoint = "https://api.openai.com/v1/chat/completions";

    public GptBasedConductChecker(@NonNull final String apiKey) {
        Objects.requireNonNull(apiKey, "apiKey must not be null");
        if (apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be blank");
        }
        this.restClient = RestClient.builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String createPrompt(@NonNull Message message) {
        Objects.requireNonNull(message, "message must not be null");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public @NonNull CheckResult check(@NonNull Message message) {
        Objects.requireNonNull(message, "message must not be null");
        final String prompt = createPrompt(message);
        final Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        final Map<String, Object> response = restClient.post()
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        throw new UnsupportedOperationException("Not implemented yet");
    }
}
