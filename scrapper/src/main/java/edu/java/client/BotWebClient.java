package edu.java.client;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.RetryConfiguration;
import edu.java.exception.ApiErrorException;
import edu.java.serviceDto.ApiErrorResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;
    private final static String DEFAULT_URL = "http://localhost:8090";


    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        addStatusCodes();
    }

    public BotWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        addStatusCodes();
    }

    @Value(value = "${api.bot.retryPolicy}")
    private String retryPolicy;
    private final Set<HttpStatus> retryStatuses = new HashSet<>();
    private Retry retry;

    private void addStatusCodes() {
        retryStatuses.add(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostConstruct
    private void configRetry() {
        retry = RetryConfiguration.config(retryPolicy, retryStatuses);
    }


    public String sendUpdate(LinkUpdateRequest request) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }

    public String sendUpdateWithRetry(LinkUpdateRequest request) {
        return Retry.decorateSupplier(retry, () -> sendUpdate(request))
            .get();
    }
}

