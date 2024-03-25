package edu.java.bot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfiguration {

    @Value(value = "${bucket.queryCount}")
    private int queryCount;

    @Bean
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(queryCount, Refill.intervally(queryCount, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
