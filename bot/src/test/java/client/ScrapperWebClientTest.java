package client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.exception.ApiErrorException;
import edu.java.bot.exception.NotValidLinkException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.URISyntaxException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
                "url":"https://github.com/example"
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
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        String response = scrapperWebClient.registerChat(message);
        assertEquals(response, "Чат зарегистрирован");
    }

    @Test
    public void registerChatBadIdTest() {
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-1L);
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.registerChat(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void registerChatDoubleRegisterTest() {
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Чат зарегистрирован")));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        String response1 = scrapperWebClient.registerChat(message);
        assertEquals(response1, "Чат зарегистрирован");
        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(DOUBLE_QUERY)));
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.registerChat(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Double query", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void deleteChatCorrectBodyTest(){
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Чат успешно удалён")));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        String response = scrapperWebClient.deleteChat(message);
        assertEquals(response, "Чат успешно удалён");
    }

    @Test
    public void deleteChatBadIdTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-1L);
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.deleteChat(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void deleteChatNotFoundTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/tg-chat/1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.deleteChat(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("404", errorResponse.getCode());
        assertEquals("Resource not found", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void getLinksCorrectBodyTest() {
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

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);

        ListLinksResponse listLinksResponse = scrapperWebClient.getLinks(message);
        Assertions.assertEquals(listLinksResponse.getSize(), 1);
        Assertions.assertEquals(listLinksResponse.getLinks().get(0).getId(), 1);
        Assertions.assertEquals(listLinksResponse.getLinks().get(0).getUrl().getPath(), "link1");
    }

    @Test
    public void getLinksBadHeaderTest(){
        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-2"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-2L);

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.getLinks(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void getLinksNotFoundIdTest(){
        wireMockServer.stubFor(get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("2"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(2L);

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.getLinks(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("404", errorResponse.getCode());
        assertEquals("Resource not found", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void AddLinkCorrectBodyTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track https://github.com/example");
        LinkResponse linkResponse = scrapperWebClient.addLink(message);
        Assertions.assertEquals(linkResponse.getId(), 1);
        System.out.println(linkResponse.getUrl());
        Assertions.assertEquals(linkResponse.getUrl().toString(), "https://github.com/example");
    }

    @Test
    public void AddLinkBadHeaderTest()  {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-1L);
        when(message.text()).thenReturn("/track https://github.com/example");
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.addLink(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void AddLinkBadBodyTest() {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track 123");
        NotValidLinkException exception = assertThrows(
            NotValidLinkException.class,
            () -> scrapperWebClient.addLink( message));
        assertEquals("It is not valid link", exception.getMessage());

    }

    @Test
    public void AddLinkNotFoundIdTest() {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track https://github.com/example");
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.addLink(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("404", errorResponse.getCode());
        assertEquals("Resource not found", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void AddLinkLinkAlreadyExistTest() throws URISyntaxException {
        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track https://github.com/example");

        LinkResponse linkResponse = scrapperWebClient.addLink(message);
        Assertions.assertEquals(linkResponse.getId(), 1);
        Assertions.assertEquals(linkResponse.getUrl().toString(), "https://github.com/example");

        wireMockServer.stubFor(post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.addLink(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void RemoveLinkCorrectBodyTest() throws URISyntaxException {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(LINK_BODY)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track https://github.com/example");

        LinkResponse linkResponse = scrapperWebClient.removeLink(message);
        Assertions.assertEquals(linkResponse.getId(), 1);
        Assertions.assertEquals(linkResponse.getUrl().toString(), "https://github.com/example");
    }

    @Test
    public void RemoveLinkBadHeaderTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("-1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(-1L);
        when(message.text()).thenReturn("/track https://github.com/example");

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.removeLink(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void RemoveLinkBadBodyTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(BAD_PARAMS)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track 123");

        NotValidLinkException exception = assertThrows(NotValidLinkException.class,
            () -> scrapperWebClient.removeLink(message));
        assertEquals("It is not valid link", exception.getMessage());
    }

    @Test
    public void RemoveLinkNotFoundIdTest() {
        wireMockServer.stubFor(delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", equalTo("1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(NOT_FOUND)));

        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(1L);
        when(message.text()).thenReturn("/track https://github.com/example");

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> scrapperWebClient.removeLink(message)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("404", errorResponse.getCode());
        assertEquals("Resource not found", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }
}
