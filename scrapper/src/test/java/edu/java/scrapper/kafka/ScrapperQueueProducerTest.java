package edu.java.scrapper.kafka;

import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.kafka.ScrapperQueueProducer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScrapperQueueProducerTest {
    @Test
    void sendTest(){

        KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate = mock(KafkaTemplate.class);
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        ApplicationConfig.Kafka kafka = mock(ApplicationConfig.Kafka.class);
        ScrapperQueueProducer scrapperQueueProducer = new ScrapperQueueProducer(kafkaTemplate, applicationConfig);
        LinkUpdateRequest update = new LinkUpdateRequest();

        when(applicationConfig.kafka()).thenReturn(kafka);
        when(applicationConfig.kafka().topicName()).thenReturn("botUpdates");
        when(kafkaTemplate.send(any(), any())).thenReturn(null);
        scrapperQueueProducer.send(update);

        verify(kafkaTemplate).send(any(), any());
    }
}
