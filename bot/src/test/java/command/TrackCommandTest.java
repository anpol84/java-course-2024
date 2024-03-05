package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.clientDto.ApiErrorResponse;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.exception.ApiErrorException;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class TrackCommandTest {

    @Test
    public void testCommand() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);

        assertEquals(trackCommand.command(),"/track");
    }

    @Test
    public void testHandleInvalidUrl() throws URISyntaxException {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/track https://stackoverflow.com/search?q=unsupported%20link");
        when(scrapperWebClient.addLink(message))
            .thenThrow(new ApiErrorException(new ApiErrorResponse("bad", "400", "name",
                "message", List.of("1", "2"))));
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        try {
            SendMessage sendMessage = trackCommand.handle(update);
        }catch (ApiErrorException e){
            assertEquals("bad", e.getErrorResponse().getDescription());
        }
    }

    @Test
    public void testHandleValidUrl() throws URISyntaxException {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/track http://stackoverflow.com/questions");
        when(scrapperWebClient.addLink(message))
            .thenReturn(new LinkResponse(1L, new URI("http://stackoverflow.com/questions")));
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        SendMessage sendMessage = trackCommand.handle(update);
        assertEquals(sendMessage.getParameters().get("text"), "The resource has been added");
    }

    @Test
    public void testDescription() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        assertEquals(trackCommand.getDescription(), "This command tracks some link");
    }
}
