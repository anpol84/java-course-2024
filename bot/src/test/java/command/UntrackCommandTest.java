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
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.exception.ApiErrorException;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class UntrackCommandTest {

    @Test
    public void testCommand() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);

        assertEquals(untrackCommand.command(), "/untrack");
    }

    @Test
    public void testHandleValidUrlNotFound() throws URISyntaxException {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");
        when(scrapperWebClient.removeLink("http://stackoverflow.com/questions", 123456789L))
            .thenThrow(new ApiErrorException(new ApiErrorResponse("bad", "400", "name",
                "message", List.of("1", "2"))));

        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);
        try {
            SendMessage sendMessage = untrackCommand.handle(update);
        }catch (ApiErrorException e){
            assertEquals("bad", e.getErrorResponse().getDescription());
        }
    }

    @Test
    public void testHandleValidUrlDeleted() throws URISyntaxException {

        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        when(message.text()).thenReturn("/untrack http://stackoverflow.com/questions");
        when(scrapperWebClient.removeLink("http://stackoverflow.com/questions", 123456789L))
            .thenReturn(new LinkResponse(1L, new URI("http://stackoverflow.com/questions")));
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);

        SendMessage sendMessage = untrackCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"), "The resource has been deleted");
    }

    @Test
    public void testDescription() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);
        assertEquals(untrackCommand.getDescription(), "This command untracks some link");
    }


}
