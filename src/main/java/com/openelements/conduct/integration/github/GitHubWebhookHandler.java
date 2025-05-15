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

import java.util.List;
import java.util.Optional;

@RestController
@ConditionalOnBean(GitHubCodeOfConductProvider.class)
public class GitHubWebhookHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GitHubWebhookHandler.class);
    
    private final Optional<GitHubCodeOfConductProvider> codeOfConductProvider;
    
    @Autowired
    public GitHubWebhookHandler(Optional<List<CodeOfConductProvider>> providers) {
        if (providers.isPresent()) {
            this.codeOfConductProvider = providers.get().stream()
                    .filter(p -> p instanceof GitHubCodeOfConductProvider)
                    .map(p -> (GitHubCodeOfConductProvider) p)
                    .findFirst();
        } else {
            this.codeOfConductProvider = Optional.empty();
        }
        
        if (codeOfConductProvider.isEmpty()) {
            log.warn("GitHubWebhookHandler initialized but no GitHubCodeOfConductProvider is available");
        }
    }
    
    @PostMapping("/github/coc-webhook")
    public void handleWebhook(@RequestHeader("X-GitHub-Event") String event, 
                             @RequestBody String payload) {
        if (codeOfConductProvider.isEmpty()) {
            log.warn("Received GitHub webhook but no GitHubCodeOfConductProvider is available");
            return;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonPayload = objectMapper.readTree(payload);

            if ("push".equals(event)) {

                if (jsonPayload.has("commits") && jsonPayload.get("commits").isArray()) {
                    for (JsonNode commit : jsonPayload.get("commits")) {
                        if (isCodeOfConductFileModified(commit)) {
                            log.info("Code of Conduct file was modified, clearing cache");
                            codeOfConductProvider.get().clearCache();
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
