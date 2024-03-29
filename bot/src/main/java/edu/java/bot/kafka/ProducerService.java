package edu.java.bot.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(applicationConfig.kafka().badResponseTopicName(), update);
    }
}
