package edu.java.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


@AllArgsConstructor
@Data
@ToString
public class StackOverflowResponse {
    private Long questionId;
    private Long answerId;
    private String ownerName;
    private String body;
    private OffsetDateTime creationDate;
}
