package edu.java.client;

import edu.java.dto.StackOverflowResponse;


public interface StackOverflowClient {
    StackOverflowResponse fetchLatestAnswer(String questionUrl);
}
