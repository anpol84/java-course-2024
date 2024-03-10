package edu.java.clientDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubResponse {

    private Long id;

    private String type;

    @JsonProperty("actor")
    private Author author;

    private Repo repo;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        @JsonProperty("name")
        private String repositoryName;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        @JsonProperty("display_login")
        private String authorName;
    }
}
