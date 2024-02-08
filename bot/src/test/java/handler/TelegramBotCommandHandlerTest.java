package handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.handler.TelegramBotCommandHandler;
import org.junit.jupiter.api.Test;


public class TelegramBotCommandHandlerTest {

    @Test
    public void testInitStartBotCalled() {
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.telegramToken()).thenReturn("someToken");
        TelegramBotCommandHandler spyHandler = spy(new TelegramBotCommandHandler(applicationConfig));
        spyHandler.init();
        verify(spyHandler).startBot();
    }

    @Test
    public void testStartBot() {
        ApplicationConfig applicationConfig = mock(ApplicationConfig.class);
        when(applicationConfig.telegramToken()).thenReturn("someToken");
        TelegramBotCommandHandler spyHandler = spy(new TelegramBotCommandHandler(applicationConfig));
        spyHandler.startBot();
        verify(spyHandler).startBot();
    }
}
