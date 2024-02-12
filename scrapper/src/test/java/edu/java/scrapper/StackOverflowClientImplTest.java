package edu.java.scrapper;




import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.GithubClient;
import edu.java.client.GithubClientImpl;
import edu.java.dto.GithubResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class StackOverflowClientImplTest {
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
    public void testFetchLatestRepositoryActivity() {

        String baseUrl = "http://localhost:" + wireMockServer.port();
        String repositoryName = "repo";
        String authorName = "anpol84";
        String responseBody = "[{\"id\":123,\"type\":\"PushEvent\",\"actor\":{\"display_login\":\"anpol84\"}," +
            "\"repo\":{\"name\":\"repo\"},\"created_at\":1644759591}]";

        wireMockServer.stubFor(get(urlEqualTo("/" + authorName + "/" + repositoryName + "/events?per_page=1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        GithubClient githubClient = new GithubClientImpl(baseUrl);

        GithubResponse response = githubClient.fetchLatestRepositoryActivity(repositoryName, authorName);

        assertNotNull(response);
        assertEquals(123L, response.getId());
        assertEquals("PushEvent", response.getType());
        assertEquals("anpol84", response.getAuthorName());
        assertEquals("repo", response.getRepositoryName());
        assertEquals(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1644759591), ZoneOffset.UTC),
            response.getCreatedAt());
    }

    @Test
    public void testFetchLatestRepositoryActivityEmptyItems() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String repositoryName = "repo";
        String authorName = "anpol84";
        String responseBody = "[]";

        wireMockServer.stubFor(get(urlEqualTo("/" + authorName + "/" + repositoryName + "/events?per_page=1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        GithubClient githubClient = new GithubClientImpl(baseUrl);

        GithubResponse response = githubClient.fetchLatestRepositoryActivity(repositoryName, authorName);

        assertNull(response);
    }

    @Test
    public void testFetchLatestRepositoryActivityInvalidJson() {
        String baseUrl = "http://localhost:" + wireMockServer.port();
        String repositoryName = "repo";
        String authorName = "anpol84";
        String responseBody = "some bad";

        wireMockServer.stubFor(get(urlEqualTo("/" + authorName + "/" + repositoryName + "/events?per_page=1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        GithubClient githubClient = new GithubClientImpl(baseUrl);

        GithubResponse response = githubClient.fetchLatestRepositoryActivity(repositoryName, authorName);

        assertNull(response);
    }
}
