package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandHolder;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
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
        CommandHolder commandHolder = new CommandHolder
            (new StartCommand(new ScrapperWebClient()),
                new ListCommand(new ScrapperWebClient()), new TrackCommand(new ScrapperWebClient()),
                new UntrackCommand(new ScrapperWebClient()));
        commandHolder.setHelpCommand(helpCommand);
        helpCommand.setCommandHolder(commandHolder);

        SendMessage sendMessage = helpCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),
            "Available commands:\n" +
                "/start - This command registers new user\n" +
                "/list - This command returns list of tracks\n" +
                "/track - This command tracks some link\n" +
                "/untrack - This command untracks some link\n" +
                "/help - This command returns list of commands\n");
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
