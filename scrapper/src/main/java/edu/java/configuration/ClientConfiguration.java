package edu.java.configuration;

import edu.java.client.BotWebClient;
import edu.java.client.GithubWebClient;
import edu.java.client.StackOverflowWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ClientConfiguration {
    @Bean
    public StackOverflowWebClient stackOverflowWebClient() {
        return new StackOverflowWebClient();
    }

    @Bean
    public GithubWebClient githubClient() {
        return new GithubWebClient();
    }

    @Bean
    public BotWebClient botWebClient() {
        return new BotWebClient();
    }
}
