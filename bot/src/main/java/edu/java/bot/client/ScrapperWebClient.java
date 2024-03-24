package edu.java.bot.client;

import edu.java.bot.clientDto.AddLinkRequest;
import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.clientDto.RemoveLinkRequest;
import edu.java.bot.configuration.RetryConfiguration;
import edu.java.bot.exception.ApiErrorException;
import edu.java.bot.exception.NotValidLinkException;
import edu.java.bot.utils.UrlUtils;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class ScrapperWebClient {
    private final WebClient webClient;
    private final static String DEFAULT_URL = "http://localhost:8080";

    private final Set<HttpStatus> retryStatuses = new HashSet<>();
    private Retry retry;
    private final static String PATH_TO_CHAT = "tg-chat/{id}";
    private final static String PATH_TO_LINK = "/links";
    private final static String HEADER_NAME = "Tg-Chat-Id";
    @Value(value = "${api.scrapper.retryPolicy}")
    private RetryPolicy retryPolicy;
    @Value(value = "${api.scrapper.constantRetry}")
    private int constantRetryCount;

    @Value(value = "${api.scrapper.linearRetry}")
    private int linearRetryCount;
    @Value(value = "${api.scrapper.exponentialRetry}")
    private int exponentialRetryCount;

    @Value(value = "${api.scrapper.linearArg}")
    private int linearFuncArg;



    public ScrapperWebClient() {
        this.webClient = WebClient.builder().baseUrl(DEFAULT_URL).build();
        addStatusCodes();
    }

    public ScrapperWebClient(String baseUrl) {

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

    public String registerChat(Long id) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }

    public String registerChatWithRetry(Long id) {
        return Retry.decorateSupplier(retry, () -> registerChat(id))
            .get();
    }

    public String deleteChat(Long id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }

    public String deleteChatWithRetry(Long id) {
        return Retry.decorateSupplier(retry, () -> deleteChat(id))
            .get();
    }

    public ListLinksResponse getLinks(Long id) {
        return webClient.get()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public ListLinksResponse getLinksWithRetry(Long id) {
        return Retry.decorateSupplier(retry, () -> getLinks(id))
            .get();
    }

    public LinkResponse addLink(String text, Long id) throws URISyntaxException {
        checkLink(text);
        AddLinkRequest request = new AddLinkRequest().setLink(new URI(text));
        return webClient.post()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse addLinkWithRetry(String text, Long id) {
        return Retry.decorateSupplier(retry, () -> {
                try {
                    return addLink(text, id);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            })
            .get();
    }


    public LinkResponse removeLink(String text, Long id) throws URISyntaxException {
        checkLink(text);
        RemoveLinkRequest request = new RemoveLinkRequest().setLink(new URI(text));
        return webClient.method(HttpMethod.DELETE)
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(id))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse removeLinkWithRetry(String text, Long id) {
        return Retry.decorateSupplier(retry, () -> {
                try {
                    return removeLink(text, id);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            })
            .get();
    }

    private void checkLink(String link) {
        if (!UrlUtils.isValidUrl(link)) {
            throw new NotValidLinkException("It is not valid link");
        }
    }
}
