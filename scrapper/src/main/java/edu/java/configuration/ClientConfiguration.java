package edu.java.configuration;

import edu.java.client.GithubWebClient;
import edu.java.client.StackOverflowWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ClientConfiguration {


    @Value(value = "${api.github.baseurl}")
    public String githubBaseurl;

    @Value(value = "${api.stackoverflow.baseurl}")
    public String stackoverflowBaseurl;

    @Value(value = "${api.github.token}")
    public String githubToken;

    @Bean
    public StackOverflowWebClient stackOverflowWebClient() {
        return new StackOverflowWebClient(stackoverflowBaseurl);
    }

    @Bean
    public GithubWebClient githubClient() {
        return new GithubWebClient(githubBaseurl, githubToken);
    }


}
