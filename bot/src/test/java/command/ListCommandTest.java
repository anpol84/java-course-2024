package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.command.Command;
import edu.java.bot.command.ListCommand;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class ListCommandTest {
    @Test
    public void testHandleNoLinks() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);

        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(scrapperWebClient.getLinks(message)).thenReturn(new ListLinksResponse(new ArrayList<>(), 0));
        ListCommand listCommand = new ListCommand(scrapperWebClient);

        SendMessage sendMessage = listCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"At the moment, no links are being tracked.");
    }

    @Test
    public void testHandleWithLinks() throws URISyntaxException {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        when(scrapperWebClient.getLinks(message)).thenReturn(new ListLinksResponse(
            List.of(new LinkResponse(1L, new URI("url1")), new LinkResponse(2L, new URI("url2"))),
            2));
        ListCommand listCommand = new ListCommand(scrapperWebClient);

        SendMessage sendMessage = listCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"Here is the list of domains and resources:\n"
        +"url1:\n" + "url2:\n");
    }

    @Test
    public void testCommand() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Command command = new ListCommand(scrapperWebClient);
        assertEquals("/list", command.command());
    }

    @Test
    public void testDescription() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Command command = new ListCommand(scrapperWebClient);
        assertEquals("This command returns list of tracks", command.getDescription());
    }
}
