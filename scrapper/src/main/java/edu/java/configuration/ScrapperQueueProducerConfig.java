package edu.java.configuration;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.kafka.ScrapperQueueProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;


@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class ScrapperQueueProducerConfig {
    @Bean
    public ScrapperQueueProducer scrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate,
        ApplicationConfig applicationConfig) {
        return new ScrapperQueueProducer(kafkaTemplate, applicationConfig);
    }
}
