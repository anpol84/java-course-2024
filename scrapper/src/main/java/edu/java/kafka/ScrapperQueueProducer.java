package edu.java.kafka;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.service.updater.UpdateSender;
import org.springframework.kafka.core.KafkaTemplate;


public class ScrapperQueueProducer implements UpdateSender {

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    private final ApplicationConfig applicationConfig;

    public ScrapperQueueProducer(
        KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate,
        ApplicationConfig applicationConfig
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationConfig = applicationConfig;
    }

    public void send(LinkUpdateRequest update) {
        kafkaTemplate.send(applicationConfig.kafka().topicName(), update);
    }
}
