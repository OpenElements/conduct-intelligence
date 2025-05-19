package com.openelements.conduct.integration.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.openelements.conduct.data.CodeOfConductProvider;
import com.openelements.conduct.data.TextfileType;
import com.openelements.conduct.endpoint.GitHubClient;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubCodeOfConductProvider implements CodeOfConductProvider {

    private static final Logger log = LoggerFactory.getLogger(GitHubCodeOfConductProvider.class);

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

    private final String codeOfConduct;

    public GitHubCodeOfConductProvider(@NonNull GitHubClient gitHubClient,
            @NonNull String owner,
            @NonNull String repo) {
        log.info("Initialized GitHub Code of Conduct provider for {}/{}", owner, repo);
        codeOfConduct = findCodeOfConduct(gitHubClient, owner, repo);
    }

    private String findCodeOfConduct(@NonNull GitHubClient gitHubClient,
            @NonNull String owner,
            @NonNull String repo) {
        return findCodeOfConduct(gitHubClient, owner, repo, "main")
                .or(() -> findCodeOfConduct(gitHubClient, owner, repo, "master"))
                .or(() -> findCodeOfConduct(gitHubClient, owner, ".github", "main"))
                .or(() -> findCodeOfConduct(gitHubClient, owner, ".github", "master"))
                .orElseThrow(() -> new RuntimeException("No code of conduct found for " + owner + " " + repo));
    }

    private Optional<String> findCodeOfConduct(@NonNull GitHubClient gitHubClient,
            @NonNull String owner,
            @NonNull String repo, @NonNull String branch) {
        Objects.requireNonNull(gitHubClient, "gitHubClient must not be null");
        Objects.requireNonNull(owner, "owner must not be null");
        Objects.requireNonNull(repo, "repo must not be null");
        Objects.requireNonNull(branch, "branch must not be null");
        if (owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }
        if (repo.isBlank()) {
            throw new IllegalArgumentException("repo must not be blank");
        }
        if (branch.isBlank()) {
            throw new IllegalArgumentException("branch must not be blank");
        }
        try {
            for (final String filename : COMMON_COC_FILENAMES) {
                try {
                    final JsonNode fileContent = gitHubClient.getRepositoryFileContent(owner, repo, filename, branch);
                    if (fileContent != null && fileContent.has("content")) {
                        log.info("Code of Conduct file {} found in {}/{}#{}", filename, owner, repo, branch);
                        final String content = fileContent.get("content").asText();
                        final String decodedContent = new String(Base64.getDecoder().decode(content.replace("\n", "")));
                        log.debug("Code of Conduct file {} decoded content: {}", filename, decodedContent);
                        return Optional.of(decodedContent);
                    } else {
                        log.debug("Code of Conduct file {} not found in {}/{}#{}", filename, owner, repo, branch);
                    }
                } catch (Exception e) {
                    log.info("Code of Conduct file {} not found in {}/{}#{}", filename, owner, repo, branch);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Code of Conduct from GitHub", e);
        }
        return Optional.empty();
    }


    @Override
    public boolean supports(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");
        return true;
    }

    @Override
    public @NonNull String getCodeOfConduct(@NonNull TextfileType type) {
        Objects.requireNonNull(type, "type must not be null");
        return codeOfConduct;
    }

}
