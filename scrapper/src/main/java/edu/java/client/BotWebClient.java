package edu.java.client;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.exception.ApiErrorException;
import edu.java.serviceDto.ApiErrorResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;
    private final static String DEFAULT_URL = "http://localhost:8090";

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    public BotWebClient(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Optional<String> sendUpdate(LinkUpdateRequest request) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::equals, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }
}
