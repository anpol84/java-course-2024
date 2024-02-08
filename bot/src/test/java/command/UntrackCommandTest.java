package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.dao.LinkDao;
import org.junit.jupiter.api.Test;

public class UntrackCommandTest {

    @Test
    public void testCommand() {
        UntrackCommand untrackCommand = new UntrackCommand(mock(LinkDao.class));

        assertEquals(untrackCommand.command(), "/untrack");
    }

    @Test
    public void testHandleInvalidUrl() {

        LinkDao linkDao = mock(LinkDao.class);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/untrack https://stackoverflow.com/search?q=unsupported%20link");

        UntrackCommand untrackCommand = new UntrackCommand(linkDao);

        SendMessage sendMessage = untrackCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"), "It is not valid link");

        verify(linkDao, never()).deleteResource(anyString(), anyString());
    }

    @Test
    public void testHandleValidUrlNotFound() {

        LinkDao linkDao = mock(LinkDao.class);
        when(linkDao.deleteResource(anyString(), anyString())).thenReturn(false);

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");

        UntrackCommand untrackCommand = new UntrackCommand(linkDao);

        SendMessage sendMessage = untrackCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"), "There is no such resource");

        verify(linkDao).deleteResource("http://stackoverflow.com", "questions");
    }

    @Test
    public void testHandleValidUrlDeleted() {

        LinkDao linkDao = mock(LinkDao.class);
        when(linkDao.deleteResource(anyString(), anyString())).thenReturn(true);

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");

        UntrackCommand untrackCommand = new UntrackCommand(linkDao);

        SendMessage sendMessage = untrackCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"), "The resource has been deleted");

        verify(linkDao).deleteResource("http://stackoverflow.com", "questions");
    }

    @Test
    public void testDescription() {

        UntrackCommand untrackCommand = new UntrackCommand(mock(LinkDao.class));

        assertEquals(untrackCommand.getDescription(), "This command untracks some link");
    }
}
