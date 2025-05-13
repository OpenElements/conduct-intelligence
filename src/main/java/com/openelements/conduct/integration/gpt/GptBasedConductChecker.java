package com.openelements.conduct.integration.gpt;

import com.openelements.conduct.data.CheckResult;
import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.ConductChecker;
import com.openelements.conduct.data.Message;
import com.openelements.conduct.data.TextfileType;
import com.openelements.conduct.data.ViolationState;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class GptBasedConductChecker implements ConductChecker {

    private final static Logger log = LoggerFactory.getLogger(GptBasedConductChecker.class);

    private final RestClient restClient;

    private final String endpoint = "https://api.openai.com/v1/chat/completions";

    private final CodeOfConductProvider codeOfConductProvider;

    public GptBasedConductChecker(@NonNull final String apiKey, @NonNull final CodeOfConductProvider codeOfConductProvider) {
        Objects.requireNonNull(apiKey, "apiKey must not be null");
        this. codeOfConductProvider = Objects.requireNonNull(codeOfConductProvider, "codeOfConductProvider must not be null");
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
        if(codeOfConductProvider.supports(TextfileType.MARKDOWN)) {
            String codeOfConduct = codeOfConductProvider.getCodeOfConduct(TextfileType.MARKDOWN);
            return  """
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
        log.info("Response: {}", response);
        return new CheckResult(
                message,
                ViolationState.NONE,
                "UNKNOWN"
        );
    }
}
