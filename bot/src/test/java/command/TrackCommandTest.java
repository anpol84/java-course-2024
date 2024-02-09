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
import edu.java.bot.command.TrackCommand;
import edu.java.bot.dao.LinkDao;
import org.junit.jupiter.api.Test;


public class TrackCommandTest {

    @Test
    public void testCommand() {
        TrackCommand trackCommand = new TrackCommand(new LinkDao());

        assertEquals(trackCommand.command(),"/track");
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
        when(message.text()).thenReturn("/track https://stackoverflow.com/search?q=unsupported%20link");
        TrackCommand trackCommand = new TrackCommand(linkDao);
        SendMessage sendMessage = trackCommand.handle(update);
        assertEquals(sendMessage.getParameters().get("text"), "It is not valid link");
        verify(linkDao, never()).addResource(anyLong(), anyString(), anyString());
    }

    @Test
    public void testHandleValidUrl() {
        LinkDao linkDao = spy(new LinkDao());
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/track http://stackoverflow.com/questions");

        TrackCommand trackCommand = new TrackCommand(linkDao);

        SendMessage sendMessage = trackCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"), "The resource has been added");

        verify(linkDao).addResource(123456789L,"http://stackoverflow.com", "questions");
    }

    @Test
    public void testDescription() {

        TrackCommand trackCommand = new TrackCommand(new LinkDao());

        assertEquals(trackCommand.getDescription(), "This command tracks some link");
    }
}
