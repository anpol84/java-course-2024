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
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandHolder;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.handler.MessageProcessor;
import edu.java.bot.handler.TelegramBotMessageProcessor;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TelegramBotMessageProcessorTest {

    private final CommandHolder commandHolder;
    {
        commandHolder = new CommandHolder();
        List<Command> commandList = new ArrayList<>();
        HelpCommand helpCommand = new HelpCommand(commandHolder);
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        StartCommand startCommand = new StartCommand(scrapperWebClient);
        ListCommand listCommand = new ListCommand(scrapperWebClient);
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);
        commandList.add(helpCommand);
        commandList.add(startCommand);
        commandList.add(listCommand);
        commandList.add(trackCommand);
        commandList.add(untrackCommand);
        commandHolder.setCommandMap(commandList);
    }

    @Test
    public void testProcessUnknownCommand() {

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

    @Test
    public void testProcessKnownCommand() {

        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor(commandHolder));
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/help");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("Available commands:\n" +
            "/list - This command returns list of tracks\n" +
            "/help - This command returns list of commands\n" +
            "/track - This command tracks some link\n" +
            "/start - This command registers new user\n" +
            "/untrack - This command untracks some link\n", result.getParameters().get("text"));
    }
}
