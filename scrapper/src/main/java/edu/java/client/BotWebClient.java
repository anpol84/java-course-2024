package edu.java.client;

import edu.java.common.exception.ApiErrorException;
import edu.java.common.requestDto.LinkUpdateRequest;
import edu.java.common.responseDto.ApiErrorResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;

    @Value(value = "${api.bot.baseurl}")
    private String baseurl;

    public BotWebClient() {
        if (baseurl == null) {
            baseurl = "http://localhost:8090";
        }
        this.webClient = WebClient.builder().baseUrl(baseurl).build();
    }

    public BotWebClient(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl.isEmpty()) {
            validatedBaseurl = this.baseurl;
        }
        this.baseurl = validatedBaseurl;
        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();

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
