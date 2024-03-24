package edu.java.client;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.RetryConfiguration;
import edu.java.exception.ApiErrorException;
import edu.java.serviceDto.ApiErrorResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;
    private final static String DEFAULT_URL = "http://localhost:8090";

    private Retry retry;
    @Value(value = "${api.bot.retryPolicy}")
    private RetryPolicy retryPolicy;
    @Value(value = "${api.bot.retryCount}")
    private int retryCount;
    @Value(value = "${api.bot.linearArg}")
    private int linearFuncArg;
    @Value("#{'${api.bot.codes}'.split(',')}")
    private Set<HttpStatus> retryStatuses;

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    public BotWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void configRetry() {
        RetryConfigDTO retryConfigDTO = new RetryConfigDTO()
            .setLinearFuncArg(linearFuncArg)
            .setRetryCount(retryCount)
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

