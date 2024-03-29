package kafka;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.kafka.ConsumerService;
import edu.java.bot.kafka.ProducerService;
import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsumerServiceTest{

    @Test
    public void testListen() {
        ProcessMessageService processMessageService = mock(ProcessMessageService.class);
        ProducerService producerService = mock(ProducerService.class);
        ConsumerService consumerService = new ConsumerService(processMessageService, producerService);
        LinkUpdateRequest update = new LinkUpdateRequest();
        doNothing().when(processMessageService).process(any());
        consumerService.listen(update);
        verify(processMessageService).process(update);
    }
}
