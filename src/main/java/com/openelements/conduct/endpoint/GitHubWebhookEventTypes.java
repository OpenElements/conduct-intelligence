package com.openelements.conduct.endpoint;

import java.util.Optional;

public enum GitHubWebhookEventTypes {

    PR_CREATED("pull_request", "opened"),
    ISSUE_CREATED("issues", "opened"),
    ISSUE_COMMENT_CREATED("issue_comment", "created"),
    DISCUSSION_CREATED("discussion", "created"),
    DISCUSSION_COMMENT_CREATED("discussion_comment", "created");

    private final String eventType;

    private final String action;

    GitHubWebhookEventTypes(final String eventType, final String action) {
        this.eventType = eventType;
        this.action = action;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAction() {
        return action;
    }

    public static Optional<GitHubWebhookEventTypes> of(final String eventType,
            final String action) {
        for (GitHubWebhookEventTypes type : GitHubWebhookEventTypes.values()) {
            if (type.getEventType().equals(eventType) && type.action.equals(action)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
