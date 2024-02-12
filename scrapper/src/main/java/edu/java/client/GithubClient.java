package edu.java.client;

import edu.java.dto.GithubResponse;


public interface GithubClient {
    GithubResponse fetchLatestRepositoryActivity(String repositoryName, String authorName);
}
