package service;

import edu.java.bot.model.BotImpl;
import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ProcessMessageServiceTest {
    @Test
    void processTest(){
        BotImpl bot = mock(BotImpl.class);
        ProcessMessageService processMessageService = new ProcessMessageService(bot);
        LinkUpdateRequest request = new LinkUpdateRequest().setTgChatIds(List.of(1L, 2L, 3L));
        doNothing().when(bot).sendMessageToChat(any(), any());
        processMessageService.process(request);
        verify(bot, times(3)).sendMessageToChat(any(), any());
    }
}
