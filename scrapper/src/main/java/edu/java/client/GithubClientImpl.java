package edu.java.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.GithubResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.web.reactive.function.client.WebClient;


public class GithubClientImpl implements GithubClient {

    private final WebClient webClient;

    public GithubClientImpl(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl == null || baseUrl.isEmpty()) {
            validatedBaseurl = "https://api.github.com/networks/";
        }
        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();
    }

    @Override
    public GithubResponse fetchLatestRepositoryActivity(String repositoryName, String authorName) {
        String completedQuestionUrl = authorName + "/" + repositoryName + "/events?per_page=1";
        return webClient.get()
            .uri(completedQuestionUrl)
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(response -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(response);
                    if (jsonNode.isArray() && jsonNode.size() > 0) {
                        JsonNode itemsNode = jsonNode.get(0);
                        long id = itemsNode.get("id").asLong();
                        String type = itemsNode.get("type").asText();
                        String authorNameAns = itemsNode.get("actor").get("display_login").asText();
                        String repositoryNameAns = itemsNode.get("repo").get("name").asText();
                        long creationDateEpochSeconds = itemsNode.get("created_at").asLong();
                        OffsetDateTime creationDate = Instant.ofEpochSecond(creationDateEpochSeconds)
                            .atOffset(ZoneOffset.UTC);

                        return new GithubResponse(id, type, repositoryNameAns, authorNameAns, creationDate);
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
            }).block();
    }
}
