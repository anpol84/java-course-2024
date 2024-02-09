package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.StartCommand;
import org.junit.jupiter.api.Test;


public class StartCommandTest {

    @Test
    public void testCommand() {
        StartCommand startCommand = new StartCommand();

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

        StartCommand startCommand = new StartCommand();

        SendMessage sendMessage = startCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),"Bot has started");
    }

    @Test
    public void testDescription() {
        StartCommand startCommand = new StartCommand();

        assertEquals(startCommand.getDescription(), "This command registers new user");
    }
}
