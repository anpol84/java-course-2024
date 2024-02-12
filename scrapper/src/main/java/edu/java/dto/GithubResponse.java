package edu.java.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@AllArgsConstructor
@Data
@ToString
public class GithubResponse {
    private Long id;
    private String type;
    private String repositoryName;
    private String authorName;
    private OffsetDateTime createdAt;
}
