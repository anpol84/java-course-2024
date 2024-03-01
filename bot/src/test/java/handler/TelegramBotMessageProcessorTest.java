package handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.command.CommandHolder;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.handler.MessageProcessor;
import edu.java.bot.handler.TelegramBotMessageProcessor;
import org.junit.jupiter.api.Test;


public class TelegramBotMessageProcessorTest {
    /*
    Тут убрал почти все тесты, т.к. по сути теперь они мало что проверяют, могу вернуть обратно, но там
    везде просто моки объектов будут, весь функционал по сути тестируется в классах коммандах
     */

    @Test
    public void testProcessUnknownCommand() {
        CommandHolder commandHolder = new CommandHolder(new StartCommand(new ScrapperWebClient()),
            new ListCommand(new ScrapperWebClient()), new TrackCommand(new ScrapperWebClient()),
            new UntrackCommand(new ScrapperWebClient()));
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.setCommandHolder(commandHolder);
        commandHolder.setHelpCommand(helpCommand);
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor(commandHolder));
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("Some command");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("Unknown command", result.getParameters().get("text"));
    }
}
