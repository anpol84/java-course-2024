package handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.handler.MessageProcessor;
import edu.java.bot.handler.TelegramBotMessageProcessor;
import org.junit.jupiter.api.Test;


public class TelegramBotMessageProcessorTest {

    @Test
    public void testProcessUnknownCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
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
    public void testProcessHelpCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
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
            "/help - This command returns list of commands\n" +
            "/start - This command registers new user\n" +
            "/list - This command returns list of tracks\n" +
            "/track - This command tracks some link\n" +
            "/untrack - This command untracks some link\n", result.getParameters().get("text"));
    }

    @Test
    public void testProcessStartCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("Bot has started", result.getParameters().get("text"));
    }

    @Test
    public void testProcessListCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/list");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("At the moment, no links are being tracked.", result.getParameters().get("text"));
    }

    @Test
    public void testProcessTrackCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/track http://stackoverflow.com/questions");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("The resource has been added", result.getParameters().get("text"));

        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");
        messageProcessor.process(update);
    }

    @Test
    public void testProcessUntrackCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNotNull(result);
        assertEquals("There is no such resource", result.getParameters().get("text"));
    }

    @Test
    public void testProcessNullCommand() {
        MessageProcessor messageProcessor = spy(new TelegramBotMessageProcessor());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);
        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        SendMessage result = messageProcessor.process(update);

        verify(messageProcessor).process(update);

        assertNull(result);
    }
}
