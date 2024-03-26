package edu.java.bot.kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    private final ProcessMessageService processMessageService;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listen(LinkUpdateRequest update) {
        try {
            processMessageService.process(update);
        } catch (Exception e) {
            kafkaTemplate.send(applicationConfig.kafka().badResponseTopicName(), update);
        }
    }

    @KafkaListener(topics = "${app.kafka.badResponseTopicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listenBadResponses(LinkUpdateRequest update) {
        log.info(update.toString());
    }
}
