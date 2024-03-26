package edu.java.kafka;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(applicationConfig.kafka().topicName(), update);
    }
}
