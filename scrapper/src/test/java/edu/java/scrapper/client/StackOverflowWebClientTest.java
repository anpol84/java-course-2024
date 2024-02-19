package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.StackOverflowClient;
import edu.java.client.StackOverflowWebClient;
import edu.java.dto.StackOverflowResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class StackOverflowWebClientTest {

    private WireMockServer wireMockServer;

    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        String baseUrl = "http://localhost:" + wireMockServer.port();
        stackOverflowClient = new StackOverflowWebClient(baseUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFetchLatestAnswer() {

        String responseBody = "{\"items\":[{\"question_id\":123,\"answer_id\":456,\"owner\":" +
            "{\"display_name\":\"anpol84\"}," +
            "\"body\":\"Answer body\",\"last_activity_date\":1644759591}]}";

        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers?pagesize=1&order=desc&" +
            "sort=activity&site=stackoverflow&filter=!2oFItoI*SdTlkIwsDm_2l37Pz08ohV1hNkDgAhyeja", 123L)))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        StackOverflowResponse response = stackOverflowClient.fetchLatestAnswer(123L).get();

        assertNotNull(response);
        assertEquals(123L, response.getQuestionId());
        assertEquals(456L, response.getAnswerId());
        assertEquals("anpol84", response.getOwner().getDisplayName());
        assertEquals("Answer body", response.getBody());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1644759591), ZoneOffset.UTC),
            response.getLastActivityDate());
    }

    @Test
    void testFetchLatestAnswerVoidItems() {

        Long questionNumber = 123L;
        String responseBody = "{\"items\":[]}";
        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers?pagesize=1&order=desc&" +
            "sort=activity&site=stackoverflow&filter=!2oFItoI*SdTlkIwsDm_2l37Pz08ohV1hNkDgAhyeja", questionNumber)))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Optional<StackOverflowResponse> response = stackOverflowClient.fetchLatestAnswer(questionNumber);
        assertFalse(response.isPresent());
    }

    @Test
    void testFetchLatestAnswerWithoutItems() {
        Long questionNumber = 123L;
        String responseBody = "some bad";

        wireMockServer.stubFor(get(urlEqualTo(String.format("/questions/%d/answers?pagesize=1&order=desc&" +
            "sort=activity&site=stackoverflow&filter=!2oFItoI*SdTlkIwsDm_2l37Pz08ohV1hNkDgAhyeja", questionNumber)))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        Optional<StackOverflowResponse> response = stackOverflowClient.fetchLatestAnswer(questionNumber);
        assertFalse(response.isPresent());
    }
}
