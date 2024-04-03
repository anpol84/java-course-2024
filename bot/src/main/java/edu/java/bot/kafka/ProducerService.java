package edu.java.bot.kafka;

import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProducerService {
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @Value(value = "${app.kafka.badResponseTopicName}")
    private String badResponseTopicName;

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(badResponseTopicName, update);
    }
}
