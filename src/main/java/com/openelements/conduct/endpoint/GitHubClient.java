package com.openelements.conduct.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitHubClient {

    private final static Logger log = LoggerFactory.getLogger(GitHubClient.class);

    @Value("${guardian.integration.github.token}")
    private String token;

    public Set<String> getAllUsersForOrg(final @NonNull String orgName) {
        Objects.requireNonNull(orgName, "orgName must not be null");
        if (orgName.isBlank()) {
            throw new IllegalArgumentException("orgName must not be blank");
        }
        try {
            final URI uri = new URI("https://api.github.com/orgs/" + orgName + "/members");
            final JsonNode node = executeGet(uri);
            Set<String> result = Set.of(node.findValuesAsText("login").toArray(new String[0]));
            log.info("Fetched {} users for organization: {}", result.size(), result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users for org: " + orgName, e);
        }
    }

    public String getDiscussionContent(final @NonNull String orgName, final @NonNull String repoName,
            final @NonNull String discussionId) {
        Objects.requireNonNull(orgName, "orgName must not be null");
        Objects.requireNonNull(repoName, "repoName must not be null");
        Objects.requireNonNull(discussionId, "discussionId must not be null");
        if (orgName.isBlank()) {
            throw new IllegalArgumentException("orgName must not be blank");
        }
        if (repoName.isBlank()) {
            throw new IllegalArgumentException("repoName must not be blank");
        }
        if (discussionId.isBlank()) {
            throw new IllegalArgumentException("discussionId must not be blank");
        }
        try {
            final URI uri = new URI("https://api.github.com/repos/" + orgName + "/" + repoName
                    + "/discussions/" + discussionId);
            final JsonNode node = executeGet(uri);
            return node.get("body").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching discussion content for org: " + orgName + ", repo: " + repoName
                    + ", discussionId: " + discussionId, e);
        }
    }

    public String getDiscussionTitle(final @NonNull String orgName, final @NonNull String repoName,
            final @NonNull String discussionId) {
        Objects.requireNonNull(orgName, "orgName must not be null");
        Objects.requireNonNull(repoName, "repoName must not be null");
        Objects.requireNonNull(discussionId, "discussionId must not be null");
        if (orgName.isBlank()) {
            throw new IllegalArgumentException("orgName must not be blank");
        }
        if (repoName.isBlank()) {
            throw new IllegalArgumentException("repoName must not be blank");
        }
        if (discussionId.isBlank()) {
            throw new IllegalArgumentException("discussionId must not be blank");
        }
        try {
            final URI uri = new URI("https://api.github.com/repos/" + orgName + "/" + repoName
                    + "/discussions/" + discussionId);
            final JsonNode node = executeGet(uri);
            return node.get("title").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching discussion title for org: " + orgName + ", repo: " + repoName
                    + ", discussionId: " + discussionId, e);
        }
    }

    private void executePost(final @NonNull URI uri, final @NonNull String payload) {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "application/vnd.github+json")
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            final String body = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return response.body();
                        } else {
                            throw new RuntimeException("Failed to fetch discussion content: " + response.statusCode());
                        }
                    }).get(10, TimeUnit.SECONDS);
            System.out.println(body);
        } catch (Exception e) {
            throw new RuntimeException("Error executing POST request to " + uri, e);
        }
    }

    private JsonNode executeGet(final @NonNull URI uri) {
        Objects.requireNonNull(uri, "URI must not be null");
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + token)
                    .header("User-Agent", "hAIro-Server")
                    .build();
            final String body = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return response.body();
                        } else {
                            throw new RuntimeException("Failed to fetch discussion content: " + response.statusCode());
                        }
                    }).get(10, TimeUnit.SECONDS);
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(body);
        } catch (Exception e) {
            throw new RuntimeException("Error executing GET request to " + uri, e);
        }
    }

    public int getContributionCount(String author) {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(
                            "https://api.github.com/search/commits?q=repo:hairo-hackers/server+author:" + author))
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer " + token)
                    .header("User-Agent", "hAIro-Server")
                    .build();
            final String body = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return response.body();
                        } else {
                            throw new RuntimeException("Failed to fetch discussion content: " + response.statusCode());
                        }
                    }).get(10, TimeUnit.SECONDS);
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(body).get("total_count").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Error executing GraphQL request", e);
        }
    }

    public JsonNode getUserInfo(String user) {
        Objects.requireNonNull(user, "user must not be null");
        if (user.isBlank()) {
            throw new IllegalArgumentException("user must not be blank");
        }
        try {
            final URI uri = new URI("https://api.github.com/users/" + user);
            return executeGet(uri);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user info for: " + user, e);
        }
    }

    public void setIssueLabel(String payload, String repoName, int issueNumber) {
        Objects.requireNonNull(payload, "payload must not be null");
        if (payload.isBlank()) {
            throw new IllegalArgumentException("payload must not be blank");
        }
        try {
            String apiUrl = "https://api.github.com/repos/" + repoName + "/issues/" + issueNumber + "/labels";

            final URI uri = new URI(apiUrl);
            executePost(uri, payload);
        } catch (Exception e) {
            throw new RuntimeException("Error setting issue label for issue: " + issueNumber, e);
        }
    }
}
