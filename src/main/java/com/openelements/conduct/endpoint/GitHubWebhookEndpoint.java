package com.openelements.conduct.endpoint;

import static com.openelements.conduct.endpoint.GitHubWebhookEventTypes.*;
import static com.openelements.conduct.endpoint.GitHubWebhookJsonParser.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openelements.conduct.WorkflowHandler;
import com.openelements.conduct.data.Message;
import java.net.URI;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubWebhookEndpoint {

    private final static Logger log = LoggerFactory.getLogger(GitHubWebhookEndpoint.class);

    public static final String HUB_EVENT = "X-GitHub-Event";

    private final WorkflowHandler workflowHandler;

    @Autowired
    public GitHubWebhookEndpoint(final @NonNull WorkflowHandler workflowHandler) {
        this.workflowHandler = Objects.requireNonNull(workflowHandler, "workflowHandler must not be null");
    }

    @PostMapping("/github/webhook")
    public void onGitHubEvent(@RequestHeader(HUB_EVENT) String eventType, @RequestBody String body) {
        log.info("Received GitHub event of type '{}': {}", eventType, body);
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode jsonNode = objectMapper.readTree(body);
            final String action = getAction(jsonNode);
            GitHubWebhookEventTypes.of(eventType, action).ifPresent(eventTypeEnum -> {
                if (eventTypeEnum == GitHubWebhookEventTypes.DISCUSSION_CREATED) {
                    final String title = getDiscussionTitle(jsonNode);
                    final String content = getDiscussionText(jsonNode);
                    final URI url = getDiscussionUrl(jsonNode);
                    final Message message = new Message(title, content, url);
                    workflowHandler.handleMessage(message);
                } else if (eventTypeEnum == DISCUSSION_COMMENT_CREATED) {
                    final String content = getComment(jsonNode);
                    final URI url = getCommentUrl(jsonNode);
                    final Message message = new Message(content, url);
                    workflowHandler.handleMessage(message);
                } else if (eventTypeEnum == ISSUE_CREATED) {
                    final String title = getIssueTitle(jsonNode);
                    final String content = getIssueText(jsonNode);
                    final URI url = getIssueUrl(jsonNode);
                    final Message message = new Message(title, content, url);
                    workflowHandler.handleMessage(message);
                } else if (eventTypeEnum == ISSUE_COMMENT_CREATED) {
                    final String content = getComment(jsonNode);
                    final URI url = getCommentUrl(jsonNode);
                    final Message message = new Message(content, url);
                    workflowHandler.handleMessage(message);
                } else if (eventTypeEnum == PR_CREATED) {
                    final String title = getPullRequestTitle(jsonNode);
                    final String content = getPullRequestText(jsonNode);
                    final URI url = getPullRequestUrl(jsonNode);
                    final Message message = new Message(title, content, url);
                    workflowHandler.handleMessage(message);
                } else {
                    log.warn("Unhandled GitHub event type: {} - {}", eventType, action);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Error in Github webhook", e);
        }
    }

}
