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
import edu.java.bot.command.StartCommand;
import edu.java.bot.exception.ApiErrorException;
import org.junit.jupiter.api.Test;
import java.util.List;


public class StartCommandTest {

    @Test
    public void testCommand() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        StartCommand startCommand = new StartCommand(scrapperWebClient);
        assertEquals(startCommand.command(), "/start");
    }

    @Test
    public void testHandle() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.message()).thenReturn(message);
        Chat chat = mock(Chat.class);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123456789L);
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        when(scrapperWebClient.registerChat(message)).thenReturn("Good");
        StartCommand startCommand = new StartCommand(scrapperWebClient);

        SendMessage sendMessage = startCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"Bot has started");

        when(scrapperWebClient.registerChat(message))
            .thenThrow(new ApiErrorException(
                new ApiErrorResponse("bad", "400", "name", "message",
                    List.of("1", "2"))));
        try {
            startCommand.handle(update);
        }catch (ApiErrorException e){
            assertEquals("bad", e.getErrorResponse().getDescription());
        }

    }

    @Test
    public void testDescription() {
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        StartCommand startCommand = new StartCommand(scrapperWebClient);
        assertEquals(startCommand.getDescription(), "This command registers new user");
    }
}
