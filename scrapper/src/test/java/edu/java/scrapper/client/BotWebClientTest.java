package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.BotWebClient;
import edu.java.common.responseDto.ApiErrorResponse;
import edu.java.common.requestDto.LinkUpdateRequest;
import edu.java.common.exception.ApiErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BotWebClientTest {
    private WireMockServer wireMockServer;

    private BotWebClient botWebClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());

        String baseUrl = "http://localhost:" + wireMockServer.port();
        botWebClient = new BotWebClient(baseUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void correctBodyTest() throws URISyntaxException {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        LinkUpdateRequest request = new LinkUpdateRequest(1L, new URI("1"), "1",list);
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Обновление обработано")));

        Optional<String> response = botWebClient.sendUpdate(request);
        assertTrue(response.isPresent());
        assertEquals(response.get(), "Обновление обработано");
    }

    @Test
    public void invalidBodyTest() {
        ArrayList<Long> list = new ArrayList<>();
        LinkUpdateRequest request = new LinkUpdateRequest(1L, null, "1",list);
        String body = """
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

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(body)));

        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> botWebClient.sendUpdate(request)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Bad params", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }

    @Test
    public void doubleUpdateTest() throws URISyntaxException {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1L);
        LinkUpdateRequest request = new LinkUpdateRequest(1L, new URI("1"), "1",list);
        String body = """
                {
                    "description":"some description",
                    "code":"400",
                    "exceptionName":"Double update",
                    "exceptionMessage":"Some mistake",
                    "stackTrace":[
                        "1",
                        "2",
                        "3"
                    ]
                }

            """;

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("Обновление обработано")));

        Optional<?> response1 = botWebClient.sendUpdate(request);
        assertTrue(response1.isPresent());
        assertEquals(response1.get().getClass(), String.class);
        assertEquals(response1.get(), "Обновление обработано");
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(body)));
        ApiErrorResponse errorResponse = assertThrows(ApiErrorException.class,
            () -> botWebClient.sendUpdate(request)).getErrorResponse();
        assertEquals("some description", errorResponse.getDescription());
        assertEquals("400", errorResponse.getCode());
        assertEquals("Double update", errorResponse.getExceptionName());
        assertEquals("Some mistake", errorResponse.getExceptionMessage());
    }
}
