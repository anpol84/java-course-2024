package edu.java.configuration;

import edu.java.client.GithubClientImpl;
import edu.java.client.StackOverflowClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Value("${api.baseurl.stackoverflow}")
    private String baseUrlStackoverflow;

    @Value("${api.baseurl.github}")
    private String baseUrlGithub;

    @Bean
    public StackOverflowClientImpl stackOverflowWebClient() {
        return new StackOverflowClientImpl(baseUrlStackoverflow);
    }

    @Bean
    public GithubClientImpl githubClient() {
        return new GithubClientImpl(baseUrlGithub);
    }
}
