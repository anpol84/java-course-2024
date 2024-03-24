package edu.java.configuration;

import edu.java.client.RetryConfigDTO;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClientResponseException;


public class RetryConfiguration {

    private RetryConfiguration() {

    }

    public static Retry config(RetryConfigDTO retryConfigDTO) {
        RetryConfig config = switch (retryConfigDTO.getRetryPolicy()) {
            case CONSTANT -> constantRetryConfig(retryConfigDTO);
            case LINEAR -> linearRetryConfig(retryConfigDTO);
            case EXPONENTIAL -> exponentialRetryConfig(retryConfigDTO);
            default -> RetryConfig.ofDefaults();
        };
        return Retry.of("my-retry", config);
    }

    private static RetryConfig constantRetryConfig(RetryConfigDTO retryConfigDTO) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(retryConfigDTO.getRetryCount())
            .waitDuration(Duration.ofSeconds(2))
            .retryOnResult(response -> retryConfigDTO.getRetryStatuses().contains(response.getStatusCode()))
            .build();
    }

    private static RetryConfig linearRetryConfig(RetryConfigDTO retryConfigDTO) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(retryConfigDTO.getRetryCount())
            .intervalFunction(IntervalFunction.of(Duration.ofSeconds(retryConfigDTO.getLinearFuncArg()),
                attempt -> retryConfigDTO.getLinearFuncArg() + 2 * attempt))
            .retryOnResult(response -> retryConfigDTO.getRetryStatuses().contains(response.getStatusCode()))
            .build();
    }

    private static RetryConfig exponentialRetryConfig(RetryConfigDTO retryConfigDTO) {
        return RetryConfig.<WebClientResponseException>custom()
            .maxAttempts(retryConfigDTO.getRetryCount())
            .intervalFunction(IntervalFunction.ofExponentialBackoff(IntervalFunction.DEFAULT_INITIAL_INTERVAL,
                IntervalFunction.DEFAULT_MULTIPLIER))
            .retryOnResult(response -> retryConfigDTO.getRetryStatuses().contains(response.getStatusCode()))
            .build();
    }

}
