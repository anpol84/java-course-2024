package edu.java.scrapper.service.updater;

import edu.java.client.BotWebClient;
import edu.java.clientDto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.kafka.ScrapperQueueProducer;
import edu.java.service.updater.SendUpdateService;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SendUpdateServiceTest {

    @Test
    void updateQueueTest(){
      ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
      ScrapperQueueProducer scrapperQueueProducer = mock(ScrapperQueueProducer.class);
      BotWebClient botWebClient = mock(BotWebClient.class);
      ApplicationConfig.Kafka kafka = mock(ApplicationConfig.Kafka.class);
      when(applicationConfig.kafka()).thenReturn(kafka);
      when(kafka.useQueue()).thenReturn(true);
      doNothing().when(scrapperQueueProducer).send(any());
      LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest();
      SendUpdateService sendUpdateService = new SendUpdateService(applicationConfig, botWebClient,
          scrapperQueueProducer);
      sendUpdateService.update(linkUpdateRequest);
      verify(scrapperQueueProducer).send(any());
      verify(botWebClient, never()).sendUpdateWithRetry(any());
    }

    @Test
    void updateClientTest(){
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        ScrapperQueueProducer scrapperQueueProducer = mock(ScrapperQueueProducer.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        ApplicationConfig.Kafka kafka = mock(ApplicationConfig.Kafka.class);
        when(applicationConfig.kafka()).thenReturn(kafka);
        when(kafka.useQueue()).thenReturn(false);
        when(botWebClient.sendUpdateWithRetry(any())).thenReturn(null);

        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest();
        SendUpdateService sendUpdateService = new SendUpdateService(applicationConfig, botWebClient,
            scrapperQueueProducer);
        sendUpdateService.update(linkUpdateRequest);
        verify(scrapperQueueProducer, never()).send(any());
        verify(botWebClient).sendUpdateWithRetry(any());
    }

}
