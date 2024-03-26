package edu.java.service.updater;

import edu.java.client.BotWebClient;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.kafka.ScrapperQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SendUpdateService {
    private final ApplicationConfig applicationConfig;
    private final BotWebClient botWebClient;
    private final ScrapperQueueProducer scrapperQueueProducer;

    public void update(LinkUpdateRequest linkUpdate) {
        if (applicationConfig.kafka().useQueue()) {
            scrapperQueueProducer.send(linkUpdate);
        } else {
            botWebClient.sendUpdateWithRetry(linkUpdate);
        }
    }
}
