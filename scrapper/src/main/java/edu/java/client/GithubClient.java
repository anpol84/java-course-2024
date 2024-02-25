package edu.java.client;

import edu.java.clientDto.GithubResponse;
import java.util.Optional;


public interface GithubClient {
    Optional<GithubResponse> fetchLatestRepositoryActivity(String repositoryName, String authorName);
}
