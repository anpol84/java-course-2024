package edu.java.bot.kafka;

import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ProcessMessageService processMessageService;
    private final ProducerService producerService;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listen(LinkUpdateRequest update) {
        try {
            processMessageService.process(update);
        } catch (Exception e) {
            producerService.send(update);
        }
    }
}
