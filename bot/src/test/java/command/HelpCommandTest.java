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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HelpCommandTest {

    private final CommandHolder commandHolder;
    {
        commandHolder = new CommandHolder();
        List<Command> commandList = new ArrayList<>();
        HelpCommand helpCommand = new HelpCommand(commandHolder);
        ScrapperWebClient scrapperWebClient = mock(ScrapperWebClient.class);
        StartCommand startCommand = new StartCommand(scrapperWebClient);
        ListCommand listCommand = new ListCommand(scrapperWebClient);
        TrackCommand trackCommand = new TrackCommand(scrapperWebClient);
        UntrackCommand untrackCommand = new UntrackCommand(scrapperWebClient);
        commandList.add(helpCommand);
        commandList.add(startCommand);
        commandList.add(listCommand);
        commandList.add(trackCommand);
        commandList.add(untrackCommand);
        commandHolder.setCommandMap(commandList);
    }

    @Test
    public void testHandle() {

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123456789L);
        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        SendMessage sendMessage = commandHolder.getCommandByName("/help").handle(update);

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
        assertEquals("/help", commandHolder.getCommandByName("/help").command());
    }

    @Test
    public void testDescription() {
        assertEquals("This command returns list of commands",
            commandHolder.getCommandByName("/help").getDescription());
    }

}
