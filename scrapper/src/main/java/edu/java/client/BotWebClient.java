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

    private final Set<HttpStatus> retryStatuses = new HashSet<>();
    private Retry retry;
    @Value(value = "${api.bot.retryPolicy}")
    private RetryPolicy retryPolicy;
    @Value(value = "${api.bot.constantRetry}")
    private int constantRetryCount;

    @Value(value = "${api.bot.linearRetry}")
    private int linearRetryCount;
    @Value(value = "${api.bot.exponentialRetry}")
    private int exponentialRetryCount;

    @Value(value = "${api.bot.linearArg}")
    private int linearFuncArg;

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        addStatusCodes();
    }

    public BotWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        addStatusCodes();
    }

    private void addStatusCodes() {
        retryStatuses.add(HttpStatus.INTERNAL_SERVER_ERROR);
        retryStatuses.add(HttpStatus.BAD_GATEWAY);
        retryStatuses.add(HttpStatus.INSUFFICIENT_STORAGE);
        retryStatuses.add(HttpStatus.SERVICE_UNAVAILABLE);
        retryStatuses.add(HttpStatus.GATEWAY_TIMEOUT);
    }

    @PostConstruct
    private void configRetry() {
        RetryConfigDTO retryConfigDTO = new RetryConfigDTO().setLinearRetryCount(linearRetryCount)
            .setConstantRetryCount(constantRetryCount)
            .setExponentialRetryCount(exponentialRetryCount)
            .setLinearFuncArg(linearFuncArg)
            .setRetryPolicy(retryPolicy)
            .setRetryStatuses(retryStatuses);
        retry = RetryConfiguration.config(retryConfigDTO);
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

