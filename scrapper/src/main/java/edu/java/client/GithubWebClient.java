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

    private final static String DEFAULT_URL = "https://api.github.com/";

    /*
    Токен чтобы делать много запросов к апи
     */
    private String token;

    public GithubWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        this.token = "";
    }

    public GithubWebClient(String baseUrl, String token) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.token = token;
    }

    @Override
    public Optional<GithubResponse> fetchLatestRepositoryActivity(String repositoryName, String authorName) {
        String completedQuestionUrl = String.format("networks/%s/%s/events", authorName, repositoryName);
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(completedQuestionUrl)
                .queryParam("per_page", 1)
                .build())
                        .header("Authorization",
                                "Bearer " + token)
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
