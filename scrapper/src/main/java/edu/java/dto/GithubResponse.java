package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
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
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repo {
        @JsonProperty("name")
        private String repositoryName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        @JsonProperty("display_login")
        private String authorName;
    }
}
