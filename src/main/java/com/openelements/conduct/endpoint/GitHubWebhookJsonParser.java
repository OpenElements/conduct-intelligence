package com.openelements.conduct.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class GitHubWebhookJsonParser {

    static URI getIssueUrl(JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("issue") && jsonNode.get("issue").has("html_url")) {
            try {
                return new URI(jsonNode.get("issue").get("html_url").asText());
            } catch (Exception e) {
                throw new RuntimeException("Error parsing URL", e);
            }
        }
        throw new IllegalArgumentException("url not found in JSON");
    }

    static String getIssueTitle(JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("issue") && jsonNode.get("issue").has("title")) {
            return jsonNode.get("issue").get("title").asText();
        }
        throw new IllegalArgumentException("title not found in JSON");
    }

    static String getIssueText(JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("issue") && jsonNode.get("issue").has("body")) {
            return jsonNode.get("issue").get("body").asText();
        }
        throw new IllegalArgumentException("title not found in JSON");
    }

    static String getAction(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("action")) {
            return jsonNode.get("action").asText();
        }
        throw new IllegalArgumentException("Action not found in JSON");
    }

    static String getDiscussionTitle(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("discussion") && jsonNode.get("discussion").has("title")) {
            return jsonNode.get("discussion").get("title").asText();
        }
        throw new IllegalArgumentException("title not found in JSON");
    }

    static String getDiscussionText(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("discussion") && jsonNode.get("discussion").has("body")) {
            return jsonNode.get("discussion").get("body").asText();
        }
        throw new IllegalArgumentException("comment not found in JSON");
    }

    static String getComment(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("comment") && jsonNode.get("comment").has("body")) {
            return jsonNode.get("comment").get("body").asText();
        }
        throw new IllegalArgumentException("comment not found in JSON");
    }

    static URI getDiscussionUrl(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("discussion") && jsonNode.get("discussion").has("html_url")) {
            try {
                return new URI(jsonNode.get("discussion").get("html_url").asText());
            } catch (Exception e) {
                throw new RuntimeException("Error parsing discussion comment URL", e);
            }
        }
        throw new IllegalArgumentException("url not found in JSON");
    }

    static URI getCommentUrl(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("comment") && jsonNode.get("comment").has("html_url")) {
            try {
                return new URI(jsonNode.get("comment").get("html_url").asText());
            } catch (Exception e) {
                throw new RuntimeException("Error parsing discussion comment URL", e);
            }
        }
        throw new IllegalArgumentException("url not found in JSON");
    }

    static URI getPullRequestUrl(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("pull_request") && jsonNode.get("pull_request").has("html_url")) {
            try {
                return new URI(jsonNode.get("pull_request").get("html_url").asText());
            } catch (Exception e) {
                throw new RuntimeException("Error parsing discussion comment URL", e);
            }
        }
        throw new IllegalArgumentException("url not found in JSON");
    }

    static String getPullRequestTitle(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("pull_request") && jsonNode.get("pull_request").has("title")) {
            return jsonNode.get("pull_request").get("title").asText();
        }
        throw new IllegalArgumentException("title not found in JSON");
    }

    static String getPullRequestText(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        if (jsonNode.has("pull_request") && jsonNode.get("pull_request").has("body")) {
            return jsonNode.get("pull_request").get("body").asText();
        }
        throw new IllegalArgumentException("comment not found in JSON");
    }

    static String getPullRequestAuthor(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        return jsonNode.get("pull_request").get("user").get("login").asText();
    }

    static String getIssueAuthor(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        return jsonNode.get("issue").get("user").get("login").asText();
    }

    static String getDiscussionAuthor(final @NonNull JsonNode jsonNode) {
        Objects.requireNonNull(jsonNode, "jsonNode must not be null");
        return jsonNode.get("discussion").get("user").get("login").asText();
    }
}
