package edu.java.client;

import edu.java.clientDto.GithubResponse;


public interface GithubClient {
    GithubResponse fetchLatestRepositoryActivity(String repositoryName, String authorName);
}
