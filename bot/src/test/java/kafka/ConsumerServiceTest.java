package kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.kafka.ConsumerService;
import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsumerServiceTest{

    @Test
    public void testListen() {
        ProcessMessageService processMessageService = mock(ProcessMessageService.class);
        KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate = mock(KafkaTemplate.class);
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        ApplicationConfig.Kafka kafka = mock(ApplicationConfig.Kafka.class);
        ConsumerService consumerService = new ConsumerService(processMessageService, kafkaTemplate, applicationConfig);
        LinkUpdateRequest update = new LinkUpdateRequest();

        when(applicationConfig.kafka()).thenReturn(kafka);
        when(applicationConfig.kafka().badResponseTopicName()).thenReturn("testBadResponseTopic");

        consumerService.listen(update);

        verify(processMessageService).process(update);

    }


}
