package com.openelements.conduct.integration.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openelements.conduct.data.CodeOfConductProvider;

/**
 * Webhook handler for GitHub events related to Code of Conduct changes.
 * This allows the cache to be refreshed when the Code of Conduct file is updated.
 */
@RestController
@ConditionalOnBean(GitHubCodeOfConductProvider.class)
public class GitHubWebhookHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookHandler.class);
    
    private final GitHubCodeOfConductProvider codeOfConductProvider;
    
    @Autowired
    public GitHubWebhookHandler(CodeOfConductProvider codeOfConductProvider) {
        if (codeOfConductProvider instanceof GitHubCodeOfConductProvider) {
            this.codeOfConductProvider = (GitHubCodeOfConductProvider) codeOfConductProvider;
        } else {
            this.codeOfConductProvider = null;
            log.warn("GitHubWebhookHandler initialized with non-GitHub CodeOfConductProvider: {}", 
                    codeOfConductProvider.getClass().getName());
        }
    }
    
    @PostMapping("/github/coc-webhook")
    public void handleWebhook(@RequestHeader("X-GitHub-Event") String event, 
                             @RequestBody String payload) {
        if (codeOfConductProvider == null) {
            log.warn("Received GitHub webhook but no GitHubCodeOfConductProvider is available");
            return;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonPayload = objectMapper.readTree(payload);
            
            // Check if this is a push event
            if ("push".equals(event)) {
                // Check if any of the modified files is a Code of Conduct file
                if (jsonPayload.has("commits") && jsonPayload.get("commits").isArray()) {
                    for (JsonNode commit : jsonPayload.get("commits")) {
                        if (isCodeOfConductFileModified(commit)) {
                            log.info("Code of Conduct file was modified, clearing cache");
                            codeOfConductProvider.clearCache();
                            return;
                        }
                    }
                }
            }
            
            log.debug("Received GitHub webhook event '{}' but no Code of Conduct file was modified", event);
        } catch (Exception e) {
            log.error("Error processing GitHub webhook", e);
        }
    }
    
    private boolean isCodeOfConductFileModified(JsonNode commit) {
        // Check modified files
        if (commit.has("modified") && commit.get("modified").isArray()) {
            for (JsonNode file : commit.get("modified")) {
                String filename = file.asText().toLowerCase();
                if (filename.contains("code_of_conduct") || 
                    filename.contains("code-of-conduct") || 
                    filename.contains("codeofconduct") ||
                    filename.equals("conduct.md") ||
                    filename.equals("conduct.txt")) {
                    return true;
                }
            }
        }
        
        // Check added files
        if (commit.has("added") && commit.get("added").isArray()) {
            for (JsonNode file : commit.get("added")) {
                String filename = file.asText().toLowerCase();
                if (filename.contains("code_of_conduct") || 
                    filename.contains("code-of-conduct") || 
                    filename.contains("codeofconduct") ||
                    filename.equals("conduct.md") ||
                    filename.equals("conduct.txt")) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
