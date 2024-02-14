package edu.java.client;

import edu.java.dto.GithubResponse;
import java.util.Optional;


public interface GithubClient {
    Optional<GithubResponse> fetchLatestRepositoryActivity(String repositoryName, String authorName);
}
