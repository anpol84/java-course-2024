package edu.java.kafka;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.service.updater.UpdateSender;
import org.springframework.kafka.core.KafkaTemplate;


public class ScrapperQueueProducer implements UpdateSender {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    private String topicName;

    public ScrapperQueueProducer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate, String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(topicName, update);
    }
}
