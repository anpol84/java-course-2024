package edu.java.bot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfiguration {

    private final static int QUERY_COUNT = 30;

    @Bean
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(QUERY_COUNT, Refill.intervally(QUERY_COUNT, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
