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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class ListCommandTest {

    @Test
    public void testHandleNoLinks() {
        LinkDao linkDao = mock(LinkDao.class);
        when(linkDao.getResources()).thenReturn(new HashMap<>());

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

        LinkDao linkDao = mock(LinkDao.class);
        Map<String, Set<String>> links = new HashMap<>();
        Set<String> resources = new HashSet<>();
        resources.add("resource1");
        resources.add("resource2");
        links.put("domain1", resources);
        when(linkDao.getResources()).thenReturn(links);

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
        LinkDao linkDao = mock(LinkDao.class);
        Command command = new ListCommand(linkDao);
        assertEquals("/list", command.command());
    }

    @Test
    public void testDescription() {
        LinkDao linkDao = mock(LinkDao.class);
        Command command = new ListCommand(linkDao);
        assertEquals("This command returns list of tracks", command.getDescription());
    }
}
