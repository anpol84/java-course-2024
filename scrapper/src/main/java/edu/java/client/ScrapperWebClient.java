package edu.java.client;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class ScrapperWebClient {
    private final WebClient webClient;

    @Value(value = "${api.scrapper.baseurl}")
    private String baseurl;

    private final static String PATH_TO_CHAT = "tg-chat/{id}";
    private final static String PATH_TO_LINK = "/links";
    private final static String HEADER_NAME = "Tg-Chat-Id";

    public ScrapperWebClient() {
        this.webClient = WebClient.builder().baseUrl(baseurl).build();
    }

    public ScrapperWebClient(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl.isEmpty()) {
            validatedBaseurl = this.baseurl;
        }
        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();
    }

    public Optional<?> registerChat(Integer id) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
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

    public Optional<?> deleteChat(Integer id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
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

    public Optional<?> getLinks(Integer id) {
        return webClient.get()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ListLinksResponse.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(ApiErrorResponse.class);
                } else {
                    return Mono.empty();
                }
            })
            .blockOptional();
    }

    public Optional<?> addLink(Integer id, AddLinkRequest request) {
        return webClient.post()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(LinkResponse.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(ApiErrorResponse.class);
                } else {
                    return Mono.empty();
                }
            })
            .blockOptional();
    }

    public Optional<?> removeLink(Integer id, RemoveLinkRequest request) {
        return webClient.method(HttpMethod.DELETE)
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(LinkResponse.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(ApiErrorResponse.class);
                } else {
                    return Mono.empty();
                }
            })
            .blockOptional();
    }
}
