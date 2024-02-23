package edu.java.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.clientDto.GithubResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.web.reactive.function.client.WebClient;


public class GithubWebClient implements GithubClient {

    private final WebClient webClient;

    public GithubWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<GithubResponse> fetchLatestRepositoryActivity(String repositoryName, String authorName) {
        String completedQuestionUrl = String.format("networks/%s/%s/events", authorName, repositoryName);
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(completedQuestionUrl)
                .queryParam("per_page", 1)
                .build())
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseResponse).block());
    }

    private GithubResponse parseResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            List<GithubResponse> responses =
                objectMapper.readValue(json, new TypeReference<>(){});
            return responses.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
