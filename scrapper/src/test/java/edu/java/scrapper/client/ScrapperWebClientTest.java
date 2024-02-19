package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.ScrapperWebClient;
import edu.java.dto.AddLinkRequest;
import edu.java.dto.ApiErrorResponse;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ScrapperWebClientTest {
    private WireMockServer wireMockServer;

    private ScrapperWebClient scrapperWebClient;

    private final static String BAD_PARAMS = """
                {
                    "description":"some description",
                    "code":"400",
                    "exceptionName":"Bad params",
                    "exceptionMessage":"Some mistake",
                    "stackTrace":[
                        "1",
                        "2",
                        "3"
                    ]
                }
            """;

    private final static String DOUBLE_QUERY = """
                {
                    "description":"some description",
                    "code":"400",
                    "exceptionName":"Double query",
                    "exceptionMessage":"Some mistake",
                    "stackTrace":[
                        "1",
                        "2",
                        "3"
                    ]
                }
            """;

    private final static String NOT_FOUND = """
                {
                    "description":"some description",
                    "code":"404",
                    "exceptionName":"Resource not found",
                    "exceptionMessage":"Some mistake",
                    "stackTrace":[
                        "1",
                        "2",
                        "3"
                    ]
                }
            """;

    private final static String LINK_BODY = """
            {
                "id":1,
                "url":"123"
            }
            """;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        String baseUrl = "http://localhost:" + wireMockServer.port();
        scrapperWebClient = new ScrapperWebClient(baseUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
    @Test
    public void registerChatCorrectBodyTest(){
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Чат зарегистрирован")));

        Optional<?> response = scrapperWebClient.registerChat(1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), String.class);
        assertEquals(response.get(), "Чат зарегистрирован");
    }

    @Test
    public void registerChatBadIdTest() {
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.registerChat(-1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void registerChatDoubleRegisterTest() {
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Чат зарегистрирован")));

        Optional<?> response1 = scrapperWebClient.registerChat(1);
        assertTrue(response1.isPresent());
        assertEquals(response1.get().getClass(), String.class);
        assertEquals(response1.get(), "Чат зарегистрирован");
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(DOUBLE_QUERY)));
        Optional<?> response = scrapperWebClient.registerChat(1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Double query");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void deleteChatCorrectBodyTest(){
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Чат успешно удалён")));

        Optional<?> response = scrapperWebClient.deleteChat(1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), String.class);
        assertEquals(response.get(), "Чат успешно удалён");
    }

    @Test
    public void deleteChatBadIdTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.deleteChat(-1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void deleteChatNotFoundTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));
        Optional<?> response = scrapperWebClient.deleteChat(1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "404");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Resource not found");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void getLinksCorrectBodyTest(){
        String body = """
            {
                "links":[
                    {
                        "id":1,
                        "url":"link1"
                    }
                ],
                "size":1
            }
            """;
        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(body)));

        Optional<?> response = scrapperWebClient.getLinks(1);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ListLinksResponse.class);
        ListLinksResponse listLinksResponse = (ListLinksResponse) response.get();
        assertEquals(listLinksResponse.getSize(), 1);
        assertEquals(listLinksResponse.getLinks().get(0).getId(), 1);
        assertEquals(listLinksResponse.getLinks().get(0).getUrl().getPath(), "link1");
    }

    @Test
    public void getLinksBadHeaderTest(){
        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-2"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.getLinks(-2);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void getLinksNotFoundIdTest(){
        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("2"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));

        Optional<?> response = scrapperWebClient.getLinks(2);
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "404");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Resource not found");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void AddLinkCorrectBodyTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Optional<?> response = scrapperWebClient.addLink(1, new AddLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), LinkResponse.class);
        LinkResponse linkResponse = (LinkResponse) response.get();
        assertEquals(linkResponse.getId(), 1);
        assertEquals(linkResponse.getUrl().getPath(), "123");
    }

    @Test
    public void AddLinkBadHeaderTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.addLink(-1, new AddLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void AddLinkBadBodyTest() {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.addLink(1, new AddLinkRequest(null));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void AddLinkNotFoundIdTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));

        Optional<?> response = scrapperWebClient.addLink(1, new AddLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "404");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Resource not found");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void AddLinkLinkAlreadyExistTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Optional<?> response = scrapperWebClient.addLink(1, new AddLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), LinkResponse.class);
        LinkResponse linkResponse = (LinkResponse) response.get();
        assertEquals(linkResponse.getId(), 1);
        assertEquals(linkResponse.getUrl().getPath(), "123");

        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response1 = scrapperWebClient.addLink(1, new AddLinkRequest(new URI("123")));
        assertTrue(response1.isPresent());
        assertEquals(response1.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response1.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void RemoveLinkCorrectBodyTest() throws URISyntaxException {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Optional<?> response = scrapperWebClient.removeLink(1, new RemoveLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), LinkResponse.class);
        LinkResponse linkResponse = (LinkResponse) response.get();
        assertEquals(linkResponse.getId(), 1);
        assertEquals(linkResponse.getUrl().getPath(), "123");
    }

    @Test
    public void RemoveLinkBadHeaderTest() throws URISyntaxException {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.removeLink(-1, new RemoveLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void RemoveLinkBadBodyTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Optional<?> response = scrapperWebClient.removeLink(1, new RemoveLinkRequest(null));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "400");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Bad params");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }

    @Test
    public void RemoveLinkNotFoundIdTest() throws URISyntaxException {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));

        Optional<?> response = scrapperWebClient.removeLink(1, new RemoveLinkRequest(new URI("123")));
        assertTrue(response.isPresent());
        assertEquals(response.get().getClass(), ApiErrorResponse.class);
        ApiErrorResponse apiErrorResponse = (ApiErrorResponse) response.get();
        assertEquals(apiErrorResponse.getCode(), "404");
        assertEquals(apiErrorResponse.getDescription(), "some description");
        assertEquals(apiErrorResponse.getExceptionName(), "Resource not found");
        assertEquals(apiErrorResponse.getExceptionMessage(), "Some mistake");
    }
}
