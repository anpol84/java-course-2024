package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.StackOverflowClient;
import edu.java.client.StackOverflowClientImpl;
import edu.java.dto.StackOverflowResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class GithubImplTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFetchLatestAnswer() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String questionUrl = "/questions/123";
        String responseBody = "{\"items\":[{\"question_id\":123,\"answer_id\":456,\"owner\":" +
            "{\"display_name\":\"John\"}," +
            "\"body\":\"Answer body\",\"creation_date\":1644759591}]}";

        wireMockServer.stubFor(get(urlEqualTo(questionUrl +
            "/answers?pagesize=1&order=desc&sort=activity&site=stackoverflow&filter=!nNPvSNdWme"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        StackOverflowClient stackOverflowClient = new StackOverflowClientImpl(baseUrl);

        StackOverflowResponse response = stackOverflowClient.fetchLatestAnswer(questionUrl);

        assertNotNull(response);
        assertEquals(123L, response.getQuestionId());
        assertEquals(456L, response.getAnswerId());
        assertEquals("John", response.getOwnerName());
        assertEquals("Answer body", response.getBody());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1644759591), ZoneOffset.UTC),
            response.getCreationDate());
    }

    @Test
    void testFetchLatestAnswerVoidItems() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String questionUrl = "/questions/123";
        String responseBody = "{\"items\":[]}";

        wireMockServer.stubFor(get(urlEqualTo(questionUrl +
            "/answers?pagesize=1&order=desc&sort=activity&site=stackoverflow&filter=!nNPvSNdWme"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        StackOverflowClient stackOverflowClient = new StackOverflowClientImpl(baseUrl);

        StackOverflowResponse response = stackOverflowClient.fetchLatestAnswer(questionUrl);
        assertNull(response);
    }

    @Test
    void testFetchLatestAnswerWithoutItems() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String questionUrl = "/questions/123";
        String responseBody = "some bad";

        wireMockServer.stubFor(get(urlEqualTo(questionUrl +
            "/answers?pagesize=1&order=desc&sort=activity&site=stackoverflow&filter=!nNPvSNdWme"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        StackOverflowClient stackOverflowClient = new StackOverflowClientImpl(baseUrl);

        StackOverflowResponse response = stackOverflowClient.fetchLatestAnswer(questionUrl);
        assertNull(response);
    }
}
