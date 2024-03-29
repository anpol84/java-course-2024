package edu.java.bot.kafka;

import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
public class DLQConsumerService {

    @KafkaListener(topics = "${app.kafka.badResponseTopicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listenBadResponses(LinkUpdateRequest update) {
        log.info(update.toString());
    }
}
