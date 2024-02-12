package edu.java.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.dto.StackOverflowResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.web.reactive.function.client.WebClient;


public class StackOverflowClientImpl implements StackOverflowClient {

    private final WebClient webClient;

    public StackOverflowClientImpl(String baseUrl) {
        String validatedBaseurl = baseUrl;
        if (baseUrl == null || baseUrl.isEmpty()) {
            validatedBaseurl = "https://api.stackexchange.com/2.3/questions/";
        }

        this.webClient = WebClient.builder().baseUrl(validatedBaseurl).build();
    }

    @Override
    public StackOverflowResponse fetchLatestAnswer(String questionUrl) {
        String completedQuestionUrl = questionUrl
            + "/answers?pagesize=1&order=desc&sort=activity&site=stackoverflow&filter=!nNPvSNdWme";
        return webClient.get()
            .uri(completedQuestionUrl)
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(response -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(response);
                    JsonNode itemsNode = jsonNode.get("items");

                    if (itemsNode == null || itemsNode.size() == 0) {
                        return null;
                    }

                    JsonNode firstItemNode = itemsNode.get(0);

                        long questionId = firstItemNode.get("question_id").asLong();
                        long answerId = firstItemNode.get("answer_id").asLong();
                        String ownerName = firstItemNode.get("owner").get("display_name").asText();
                        String body = firstItemNode.get("body").asText();
                        long creationDateEpochSeconds = firstItemNode.get("creation_date").asLong();

                        OffsetDateTime creationDate = Instant.ofEpochSecond(creationDateEpochSeconds)
                            .atOffset(ZoneOffset.UTC);

                        return new StackOverflowResponse(questionId, answerId, ownerName, body, creationDate);

                } catch (Exception e) {
                    return null;
                }
            }).block();
    }
}
