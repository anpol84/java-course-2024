package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class HelpCommandTest {

    @Test
    public void testHandle() {
        Command command1 = mock(Command.class);
        when(command1.command()).thenReturn("/command1");
        when(command1.getDescription()).thenReturn("Description 1");

        Command command2 = mock(Command.class);
        when(command2.command()).thenReturn("/command2");
        when(command2.getDescription()).thenReturn("Description 2");

        List<Command> commands = new ArrayList<>();
        commands.add(command1);
        commands.add(command2);

        Chat chat = mock(Chat.class);
        when(chat.id()).thenReturn(123456789L);
        Message message = mock(Message.class);
        when(message.chat()).thenReturn(chat);
        Update update = mock(Update.class);
        when(update.message()).thenReturn(message);

        HelpCommand helpCommand = new HelpCommand(commands);

        SendMessage sendMessage = helpCommand.handle(update);

        assertEquals(sendMessage.getParameters().get("text"),
            ("Available commands:\n/command1 - Description 1\n/command2 - Description 2\n"));

        verify(command1).command();
        verify(command2).command();

        verify(command1).getDescription();
        verify(command2).getDescription();
    }

    @Test
    public void testCommand() {
        List<Command> commands = mock(List.class);
        Command command = new HelpCommand(commands);
        assertEquals("/help", command.command());
    }

    @Test
    public void testDescription() {
        List<Command> commands = mock(List.class);
        Command command = new HelpCommand(commands);
        assertEquals("This command returns list of commands", command.getDescription());
    }
}
