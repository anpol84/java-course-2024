package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.ListCommand;
import edu.java.bot.dao.LinkDao;
import org.junit.jupiter.api.Test;



public class ListCommandTest {

    @Test
    public void testHandleNoLinks() {
        LinkDao linkDao = new LinkDao();

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);

        ListCommand listCommand = new ListCommand(linkDao);

        SendMessage sendMessage = listCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"At the moment, no links are being tracked.");
    }

    @Test
    public void testHandleWithLinks() {

        LinkDao linkDao = new LinkDao();
        linkDao.addResource(123456789L, "domain1","resource1" );
        linkDao.addResource(123456789L, "domain1","resource2" );

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        ListCommand listCommand = new ListCommand(linkDao);

        SendMessage sendMessage = listCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"Here is the list of domains and resources:\n" +
            "domain1:\ndomain1/resource2\ndomain1/resource1\n");
    }

    @Test
    public void testCommand() {
        LinkDao linkDao = new LinkDao();
        Command command = new ListCommand(linkDao);
        assertEquals("/list", command.command());
    }

    @Test
    public void testDescription() {
        LinkDao linkDao = new LinkDao();
        Command command = new ListCommand(linkDao);
        assertEquals("This command returns list of tracks", command.getDescription());
    }
}
