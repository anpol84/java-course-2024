package edu.java.client;

import edu.java.clientDto.StackOverflowResponse;
import java.util.Optional;


public interface StackOverflowClient {
    Optional<StackOverflowResponse> fetchLatestAnswer(Long questionNumber);
}
