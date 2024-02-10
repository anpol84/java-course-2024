package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import org.junit.jupiter.api.Test;


public class HelpCommandTest {

    @Test
    public void testHandle() {

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123456789L);
        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        HelpCommand helpCommand = new HelpCommand();

        SendMessage sendMessage = helpCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),
            "Available commands:\n" +
                "/help - This command returns list of commands\n" +
                "/start - This command registers new user\n" +
                "/list - This command returns list of tracks\n" +
                "/track - This command tracks some link\n" +
                "/untrack - This command untracks some link\n");
    }

    @Test
    public void testCommand() {
        Command command = new HelpCommand();
        assertEquals("/help", command.command());
    }

    @Test
    public void testDescription() {
        Command command = new HelpCommand();
        assertEquals("This command returns list of commands", command.getDescription());
    }
}
