package edu.java.clientDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StackOverflowResponse {
    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("answer_id")
    private Long answerId;

    private Owner owner;

    private String body;

    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {
        @JsonProperty("display_name")
        private String displayName;
    }
}


