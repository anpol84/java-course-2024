package edu.java.configuration;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class RetryConfiguration {

    private final static int RETRY_COUNT = 3;

    private final static int LINEAR_FUNC_ARG = 3;

    private RetryConfiguration() {

    }

    public static Retry config(String retryPolicy, Set<HttpStatus> retryStatuses) {
        RetryConfig config;
        if (retryPolicy.equals("constant")) {
            config = RetryConfig.<WebClientResponseException>custom()
                .maxAttempts(RETRY_COUNT)
                .waitDuration(Duration.ofSeconds(2))
                .retryOnResult(response -> retryStatuses.contains(response.getStatusCode()))
                .build();
        } else if (retryPolicy.equals("linear")) {
            config = RetryConfig.<WebClientResponseException>custom()
                .maxAttempts(RETRY_COUNT)
                .intervalFunction(IntervalFunction.of(Duration.ofSeconds(LINEAR_FUNC_ARG),
                    attempt -> LINEAR_FUNC_ARG + 2 * attempt))
                .retryOnResult(response -> retryStatuses.contains(response.getStatusCode()))
                .build();
        } else if (retryPolicy.equals("exponential")) {
            config = RetryConfig.<WebClientResponseException>custom()
                .maxAttempts(RETRY_COUNT)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(IntervalFunction.DEFAULT_INITIAL_INTERVAL,
                    IntervalFunction.DEFAULT_MULTIPLIER))
                .retryOnResult(response -> retryStatuses.contains(response.getStatusCode()))
                .build();
        } else {
            config = RetryConfig.ofDefaults();
        }
        return Retry.of("my-retry", config);
    }
}
