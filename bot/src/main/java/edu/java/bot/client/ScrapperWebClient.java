package edu.java.bot.client;

import edu.java.bot.clientDto.AddLinkRequest;
import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.clientDto.RemoveLinkRequest;
import edu.java.bot.exception.ApiErrorException;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class ScrapperWebClient {
    private final WebClient webClient;

    private final static String DEFAULT_URL = "http://localhost:8080";

    private final static String PATH_TO_CHAT = "tg-chat/{id}";
    private final static String PATH_TO_LINK = "/links";
    private final static String HEADER_NAME = "Tg-Chat-Id";

    public ScrapperWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
    }

    public ScrapperWebClient(String baseUrl) {

        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Optional<String> registerChat(Long id) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public Optional<String> deleteChat(Long id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public Optional<ListLinksResponse> getLinks(Long id) {
        return webClient.get()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(ListLinksResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> addLink(Long id, AddLinkRequest request) {
        return webClient.post()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> removeLink(Long id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }
}
