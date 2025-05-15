package com.openelements.conduct.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import com.openelements.conduct.endpoint.GitHubClient;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GitHubCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(GitHubCodeOfConductProvider.class);

    private final GitHubClient gitHubClient;
    private final String owner;
    private final String repo;
    private final String branch;
    
    // Cache for Code of Conduct content to avoid frequent API calls
    private final ConcurrentHashMap<TextfileType, CachedContent> contentCache = new ConcurrentHashMap<>();
    
    // Cache expiration time in minutes
    private final long cacheExpirationMinutes;

    private static final String DEFAULT_BRANCH = "main";
    private static final String[] COMMON_COC_FILENAMES = {
            "CODE_OF_CONDUCT.md",
            "CODE_OF_CONDUCT.txt",
            "CODE_OF_CONDUCT",
            "CODE-OF-CONDUCT.md",
            "code-of-conduct.md",
            "code_of_conduct.md",
            "CONDUCT.md",
            "CONDUCT.txt"
    };

    public GitHubCodeOfConductProvider(@NonNull GitHubClient gitHubClient, 
                                      @NonNull String owner, 
                                      @NonNull String repo, 
                                      String branch,
                                      long cacheExpirationMinutes) {
        this.gitHubClient = Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        this.owner = Objects.requireNonNull(owner, "owner must not be null");
        this.repo = Objects.requireNonNull(repo, "repo must not be null");
        this.branch = StringUtils.hasText(branch) ? branch : DEFAULT_BRANCH;
        this.cacheExpirationMinutes = cacheExpirationMinutes > 0 ? cacheExpirationMinutes : 60; // Default to 60 minutes
        
        if (owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }
        if (repo.isBlank()) {
            throw new IllegalArgumentException("repo must not be blank");
        }
        
        log.info("Initialized GitHub Code of Conduct provider for {}/{} (branch: {})", owner, repo, this.branch);
    }
    
    public GitHubCodeOfConductProvider(@NonNull GitHubClient gitHubClient, 
                                      @NonNull String owner, 
                                      @NonNull String repo, 
                                      String branch) {
        this(gitHubClient, owner, repo, branch, 60);
    }

    @Override
    public boolean supports(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");

        if (contentCache.containsKey(type)) {
            CachedContent cachedContent = contentCache.get(type);
            if (!cachedContent.isExpired()) {
                return true;
            }
        }

        if (type == TextfileType.MARKDOWN) {
            return findCodeOfConductFile().isPresent();
        }

        if (type == TextfileType.PLAIN) {
            return findCodeOfConductFile().isPresent();
        }

        if (type == TextfileType.HTML) {
            return findCodeOfConductFile().isPresent();
        }
        
        return false;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");

        if (contentCache.containsKey(type)) {
            CachedContent cachedContent = contentCache.get(type);
            if (!cachedContent.isExpired()) {
                log.debug("Returning cached Code of Conduct content for type: {}", type);
                return cachedContent.getContent();
            }
        }
        
        String filename = findCodeOfConductFile().orElseThrow(() -> 
            new IllegalStateException("No Code of Conduct file found in repository " + owner + "/" + repo));
        
        try {
            JsonNode fileContent = gitHubClient.getRepositoryFileContent(owner, repo, filename, branch);
            
            if (fileContent != null && fileContent.has("content")) {
                String content = fileContent.get("content").asText();

                String decodedContent = new String(Base64.getDecoder().decode(content.replace("\n", "")));

                contentCache.put(type, new CachedContent(decodedContent, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(cacheExpirationMinutes)));
                
                return decodedContent;
            } else {
                throw new IllegalStateException("Failed to retrieve Code of Conduct content");
            }
        } catch (Exception e) {
            log.error("Error fetching Code of Conduct from GitHub", e);
            throw new RuntimeException("Failed to fetch Code of Conduct from GitHub: " + e.getMessage(), e);
        }
    }
    

    public void clearCache() {
        contentCache.clear();
        log.info("Cleared Code of Conduct content cache");
    }
    
    private Optional<String> findCodeOfConductFile() {
        try {
            // Try to find the Code of Conduct file in the repository
            for (String filename : COMMON_COC_FILENAMES) {
                try {
                    JsonNode fileContent = gitHubClient.getRepositoryFileContent(owner, repo, filename, branch);
                    if (fileContent != null && fileContent.has("content")) {
                        log.debug("Found Code of Conduct file: {}", filename);
                        return Optional.of(filename);
                    }
                } catch (Exception e) {
                    // File not found, try next filename
                    log.debug("Code of Conduct file {} not found in {}/{}", filename, owner, repo);
                }
            }
            
            // If we couldn't find a file with the common names, try to check if the repository
            // has a .github folder with a CODE_OF_CONDUCT.md file
            try {
                JsonNode fileContent = gitHubClient.getRepositoryFileContent(owner, repo, ".github/CODE_OF_CONDUCT.md", branch);
                if (fileContent != null && fileContent.has("content")) {
                    log.debug("Found Code of Conduct file in .github folder");
                    return Optional.of(".github/CODE_OF_CONDUCT.md");
                }
            } catch (Exception e) {
                log.debug("Code of Conduct file not found in .github folder for {}/{}", owner, repo);
            }
            
            // Try to check if there's an organization-level Code of Conduct
            if (!owner.equals("OpenElements")) {
                try {
                    // Check if the organization has a .github repository with a CODE_OF_CONDUCT.md file
                    JsonNode fileContent = gitHubClient.getRepositoryFileContent(owner, ".github", "CODE_OF_CONDUCT.md", "main");
                    if (fileContent != null && fileContent.has("content")) {
                        log.debug("Found organization-level Code of Conduct file in {}'s .github repository", owner);
                        return Optional.of("CODE_OF_CONDUCT.md");
                    }
                } catch (Exception e) {
                    log.debug("Organization-level Code of Conduct file not found for {}", owner);
                }
            }
            
            log.warn("No Code of Conduct file found in repository {}/{}", owner, repo);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error searching for Code of Conduct file", e);
            return Optional.empty();
        }
    }
    
    /**
     * Helper class to store cached content with expiration time.
     */
    private static class CachedContent {
        private final String content;
        private final long expirationTime;
        
        public CachedContent(String content, long expirationTime) {
            this.content = content;
            this.expirationTime = expirationTime;
        }
        
        public String getContent() {
            return content;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
