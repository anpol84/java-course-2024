package edu.java.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.clientDto.StackOverflowResponse;
import edu.java.configuration.RetryConfiguration;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;


public class StackOverflowWebClient implements StackOverflowClient {

    private final WebClient webClient;
    private final static String DEFAULT_URL = "https://api.stackexchange.com/2.3/";

    @Value(value = "${api.stackoverflow.retryPolicy}")
    private String retryPolicy;
    private final Set<HttpStatus> retryStatuses = new HashSet<>();
    private Retry retry;

    public StackOverflowWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        addStatusCodes();
    }

    public StackOverflowWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
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
    public StackOverflowResponse fetchLatestAnswer(Long questionNumber) {
        String completedQuestionUrl = String.format("questions/%s/answers", questionNumber);
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(completedQuestionUrl)
                .queryParam("pagesize", 1)
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .queryParam("filter", "!2oFItoI*SdTlkIwsDm_2l37Pz08ohV1hNkDgAhyeja")
                .build()
            )
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseResponse).block();
    }

    public StackOverflowResponse fetchLatestAnswerWithRetry(Long questionNumber) {
        return Retry.decorateSupplier(retry, () -> fetchLatestAnswer(questionNumber))
            .get();
    }

    private StackOverflowResponse parseResponse(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Map<String, List<StackOverflowResponse>> responses
                = objectMapper.readValue(json, new TypeReference<>(){});
            return responses.get("items").get(0);
        } catch (Exception e) {
            return null;
        }
    }
}
