package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowResponse {
    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("answer_id")
    private Long answerId;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("body")
    private String body;

    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {
        @JsonProperty("display_name")
        private String displayName;
    }
}


