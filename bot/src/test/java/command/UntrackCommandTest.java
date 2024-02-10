package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.dao.LinkDao;
import org.junit.jupiter.api.Test;
;

public class UntrackCommandTest {

    @Test
    public void testCommand() {
        UntrackCommand untrackCommand = new UntrackCommand(new LinkDao());

        assertEquals(untrackCommand.command(), "/untrack");
    }

    @Test
    public void testHandleInvalidUrl() {

        LinkDao linkDao = spy(new LinkDao());
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

        verify(linkDao, never()).deleteResource(anyLong(), anyString(), anyString());
    }

    @Test
    public void testHandleValidUrlNotFound() {

        LinkDao linkDao = spy(new LinkDao());
        when(linkDao.deleteResource(anyLong(), anyString(), anyString())).thenReturn(false);

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

        verify(linkDao).deleteResource(123456789L, "http://stackoverflow.com", "questions");
    }

    @Test
    public void testHandleValidUrlDeleted() {

        LinkDao linkDao = spy(new LinkDao());
        when(linkDao.deleteResource(anyLong(), anyString(), anyString())).thenReturn(true);

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

        verify(linkDao).deleteResource(123456789L,"http://stackoverflow.com", "questions");
    }

    @Test
    public void testDescription() {

        UntrackCommand untrackCommand = new UntrackCommand(new LinkDao());

        assertEquals(untrackCommand.getDescription(), "This command untracks some link");
    }
}
