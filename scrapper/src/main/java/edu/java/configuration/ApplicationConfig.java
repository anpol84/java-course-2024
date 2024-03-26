package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;


@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableScheduling
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    String databaseAccessType,

    @NotNull
    Kafka kafka
) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay,
                            @NotNull Duration unusedLinksInterval) {
    }

    public record Kafka(boolean useQueue, String bootstrapServers, Producer producer,
                        String topicName) {
        public record Producer(String keySerializer, String valueSerializer){}
    }


}
