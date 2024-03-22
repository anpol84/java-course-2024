package edu.java.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.clientDto.GithubResponse;
import edu.java.configuration.RetryConfiguration;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;


public class GithubWebClient implements GithubClient {

    private final WebClient webClient;
    private final Set<HttpStatus> retryStatuses = new HashSet<>();

    private final static String DEFAULT_URL = "https://api.github.com/";
    private Retry retry;

    @Value(value = "${api.github.retryPolicy}")
    private String retryPolicy;

    private final String token;

    public GithubWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        this.token = "";
        addStatusCodes();
    }

    public GithubWebClient(String baseUrl, String token) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.token = token;
        addStatusCodes();
    }

    private void addStatusCodes() {
        retryStatuses.add(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostConstruct
    private void configRetry() {
        retry = RetryConfiguration.config(retryPolicy, retryStatuses);
    }

    @Override
    public GithubResponse fetchLatestRepositoryActivity(String repositoryName, String authorName) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime currentTime = ZonedDateTime.now(zoneId);
        String timeZoneHeader = currentTime.getZone().toString();
        String completedQuestionUrl = String.format("networks/%s/%s/events", authorName, repositoryName);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(completedQuestionUrl)
                .queryParam("per_page", 1)
                .build())
            .header("Authorization", "Bearer " + token)
            .header("Time-Zone", timeZoneHeader)
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseResponse).block();
    }

    public GithubResponse fetchLatestRepositoryActivityWithRetry(String repositoryName, String authorName) {
        return Retry.decorateSupplier(retry, () -> fetchLatestRepositoryActivity(repositoryName, authorName))
            .get();
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
