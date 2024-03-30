package edu.java.configuration;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.kafka.ScrapperQueueProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;


@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class ScrapperQueueProducerConfig {

    @Value(value = "${app.kafka.topicName}")
    private String topicName;

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        return new ScrapperQueueProducer(kafkaTemplate, topicName);
    }
}
