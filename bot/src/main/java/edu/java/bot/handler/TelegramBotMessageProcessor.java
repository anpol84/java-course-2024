package edu.java.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TelegramBotMessageProcessor implements MessageProcessor {
    private final CommandHolder commandHolder;

    @Override
    public List<? extends Command> commands() {
        return commandHolder.getCommands();
    }

    @Override
    public SendMessage process(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String commandName = update.message().text().split(" ")[0];
            Command command = commandHolder.getCommandByName(commandName);
            if (command != null && command.supports(update)) {
                return command.handle(update);
            }
            return new SendMessage(update.message().chat().id(), "Unknown command");
        }
        return null;
    }
}
