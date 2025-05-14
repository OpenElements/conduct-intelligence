package com.openelements.conduct.integration.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.TextfileType;
import com.openelements.conduct.data.ViolationState;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class OpenAiBasedConductChecker implements ConductChecker {

    private final static Logger log = LoggerFactory.getLogger(OpenAiBasedConductChecker.class);

    private final RestClient restClient;

    private final String endpoint;

    private final String apiKey;

    private final CodeOfConductProvider codeOfConductProvider;

    private final String model;

    public OpenAiBasedConductChecker(@NonNull final String endpoint,
            @NonNull final String apiKey,
            @NonNull final String model,
            @NonNull final CodeOfConductProvider codeOfConductProvider) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey must not be null");
        if (apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be blank");
        }
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint must not be null");
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.codeOfConductProvider = Objects.requireNonNull(codeOfConductProvider,
                "codeOfConductProvider must not be null");

        log.info("Using OpenAI API with model: {}", model);
        log.info("Using OpenAI API with endpoint: {}", endpoint);

        this.restClient = RestClient.builder()
                .baseUrl(endpoint)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String createPrompt(@NonNull Message message) {
        Objects.requireNonNull(message, "message must not be null");
        if (codeOfConductProvider.supports(TextfileType.MARKDOWN)) {
            String codeOfConduct = codeOfConductProvider.getCodeOfConduct(TextfileType.MARKDOWN);
            return """
                    You are a code of conduct checker for an open source project.
                    Your task is to check if the following message violates the code of conduct of the project.
                    
                    Your answer must be in a JSON format with the following fields:
                    - result: the result of the check. Allowed values are NONE, POSSIBLE_VIOLATION or VIOLATION
                    - reason: a short text explaining the result
                    
                    The message has the title (can be null and should be ignored than): %s
                    The message has the text: %s
                    
                    The code of conduct is:
                    %s
                    """.formatted(message.title(), message.message(), codeOfConduct);
        } else {
            throw new UnsupportedOperationException("Not implemented yet other texttype than markdown.");
        }
    }

    @Override
    public @NonNull CheckResult check(@NonNull final Message message) {
        final String prompt = createPrompt(message);
        final JsonNode jsonNode = calOpenAIEndpoint(prompt);
        final String result = jsonNode.get("result").asText();
        final String reason = jsonNode.get("reason").asText();
        final ViolationState violationState = ViolationState.valueOf(result);
        return new CheckResult(
                message,
                violationState,
                reason
        );
    }

    @Nullable
    private JsonNode calOpenAIEndpoint(@NotNull final String prompt) {
        Objects.requireNonNull(prompt, "prompt must not be null");
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("model", model);
            final ArrayNode messagesNode = objectMapper.createArrayNode();
            final ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompt);
            messagesNode.add(messageNode);
            requestNode.set("messages", messagesNode);

            log.info("Request to OpenAI API: {}", requestNode.toPrettyString());

            final HttpClient httpClient = HttpClient.newBuilder()
                    .build();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestNode.toPrettyString()))
                    .build();
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            final JsonNode responseNode = objectMapper.readTree(response.body());

            if (responseNode == null) {
                throw new IllegalStateException("Response from OpenAI API is null");
            }
            if (!responseNode.has("choices")) {
                throw new IllegalStateException("Response from OpenAI API does not contain 'choices'");
            }
            final JsonNode choicesNode = responseNode.get("choices");
            if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
                throw new IllegalStateException("Response from OpenAI API does not contain valid 'choices'");
            }
            if (choicesNode.size() > 1) {
                log.warn("Warning: More than one choice found in the response. Using the first one.");
            }
            final JsonNode firstChoice = choicesNode.get(0);
            if (!firstChoice.has("message")) {
                throw new IllegalStateException("Response from OpenAI API does not contain 'message'");
            }
            if (!firstChoice.get("message").has("content")) {
                throw new IllegalStateException("Response from OpenAI API does not contain 'content'");
            }
            return firstChoice.get("message").get("content");
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API", e);
        }
    }
}
