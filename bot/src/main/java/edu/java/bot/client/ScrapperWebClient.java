package edu.java.bot.client;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.clientDto.AddLinkRequest;
import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.clientDto.RemoveLinkRequest;
import edu.java.bot.exception.ApiErrorException;
import edu.java.bot.exception.NotValidLinkException;
import edu.java.bot.utils.UrlUtils;
import java.net.URI;
import java.net.URISyntaxException;
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

    public String registerChat(Message message) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(message.chat().id()))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }

    public String deleteChat(Message message) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder.path(PATH_TO_CHAT).build(message.chat().id()))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(String.class)
            .block();
    }

    public ListLinksResponse getLinks(Message message) {
        return webClient.get()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(message.chat().id()))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public LinkResponse addLink(Message message) throws URISyntaxException {
        String messageText = message.text().split(" ")[1];
        checkLink(messageText);
        AddLinkRequest request = new AddLinkRequest(new URI(messageText));
        return webClient.post()
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(message.chat().id()))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public LinkResponse removeLink(Message message) throws URISyntaxException {
        String messageText = message.text().split(" ")[1];
        checkLink(messageText);
        RemoveLinkRequest request = new RemoveLinkRequest(new URI(messageText));
        return webClient.method(HttpMethod.DELETE)
            .uri(PATH_TO_LINK)
            .header(HEADER_NAME, String.valueOf(message.chat().id()))
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(ApiErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new ApiErrorException(errorResponse)))
            )
            .bodyToMono(LinkResponse.class)
            .block();
    }

    private void checkLink(String link) {
        if (!UrlUtils.isValidUrl(link)) {
            throw new NotValidLinkException("It is not valid link");
        }
    }
}
