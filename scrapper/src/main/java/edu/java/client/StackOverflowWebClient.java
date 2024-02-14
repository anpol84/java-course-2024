package edu.java.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.dto.StackOverflowResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;


public class StackOverflowWebClient implements StackOverflowClient {

    @Value(value = "api.stackoverflow.baseurl")
    private String baseurl;

    private final WebClient webClient;

    public StackOverflowWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseurl).build();
    }

    public StackOverflowWebClient(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl.isEmpty()) {
            validatedBaseurl = this.baseurl;
        }
        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();
    }

    @Override
    public Optional<StackOverflowResponse> fetchLatestAnswer(Long questionNumber) {
        String completedQuestionUrl = String.format("questions/%s/answers", questionNumber);
        return Optional.ofNullable(webClient.get()
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
            .mapNotNull(this::parseResponse).block());
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
