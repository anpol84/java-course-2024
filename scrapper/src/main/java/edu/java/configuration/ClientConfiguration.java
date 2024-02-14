package edu.java.configuration;

import edu.java.client.GithubWebClient;
import edu.java.client.StackOverflowWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ClientConfiguration {
    @Value("${api.stackoverflow.baseurl}")
    private String baseUrlStackoverflow;

    @Value("${api.github.baseurl}")
    private String baseUrlGithub;

    @Bean
    public StackOverflowWebClient stackOverflowWebClient() {
        return new StackOverflowWebClient(baseUrlStackoverflow);
    }

    @Bean
    public GithubWebClient githubClient() {
        return new GithubWebClient(baseUrlGithub);
    }
}
