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
import java.util.HashMap;
import java.util.Map;


public class HelpCommandTest {
    private final Map<String, Command> commandMap;
    {
        CommandHolder commandHolder = new CommandHolder();

        commandMap = new HashMap<>();

        HelpCommand helpCommand = new HelpCommand(commandHolder);
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        StartCommand startCommand = new StartCommand(scrapperWebClient);
        ListCommand listCommand = new ListCommand(scrapperWebClient);
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);
        commandMap.put(helpCommand.command(), helpCommand);
        commandMap.put(startCommand.command(), startCommand);
        commandMap.put(listCommand.command(), listCommand);
        commandMap.put(trackCommand.command(), trackCommand);
        commandMap.put(untrackCommand.command(), untrackCommand);
        commandHolder.setCommandMap(commandMap);
    }

    @Test
    public void testHandle() {

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123456789L);
        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        SendMessage sendMessage = commandMap.get("/help").handle(update);

        assertEquals(sendMessage.getParameters().get("text"),
            "Available commands:\n" +
                "/list - This command returns list of tracks\n" +
                "/help - This command returns list of commands\n" +
                "/track - This command tracks some link\n" +
                "/start - This command registers new user\n" +
                "/untrack - This command untracks some link\n");
    }

    @Test
    public void testCommand() {
        assertEquals("/help", commandMap.get("/help").command());
    }

    @Test
    public void testDescription() {
        assertEquals("This command returns list of commands", commandMap.get("/help").getDescription());
    }
}
