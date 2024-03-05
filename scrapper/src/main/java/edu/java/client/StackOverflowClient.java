package edu.java.client;

import edu.java.clientDto.StackOverflowResponse;


public interface StackOverflowClient {
    StackOverflowResponse fetchLatestAnswer(Long questionNumber);
}
