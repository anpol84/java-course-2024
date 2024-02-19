package edu.java.client;

import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkUpdateRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class BotWebClient {

    private final WebClient webClient;

    @Value(value = "${api.bot.baseurl}")
    private String baseurl;

    public BotWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseurl).build();
    }

    public BotWebClient(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl.isEmpty()) {
            validatedBaseurl = this.baseurl;
        }
        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();
    }

    public Optional<?> sendUpdate(LinkUpdateRequest request) {
        return webClient.post()
            .uri("/updates")
            .body(BodyInserters.fromValue(request))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(String.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(ApiErrorResponse.class);
                } else {
                    return Mono.empty();
                }
            })
            .blockOptional();
    }
}
