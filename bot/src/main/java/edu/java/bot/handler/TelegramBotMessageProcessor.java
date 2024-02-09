package edu.java.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandsList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TelegramBotMessageProcessor implements MessageProcessor {

    Map<String, Command> commandMap = new HashMap<>();

    public TelegramBotMessageProcessor() {
        for (Command command : CommandsList.getCommands()) {
            commandMap.put(command.command(), command);
        }
    }

    @Override
    public List<? extends Command> commands() {
        return CommandsList.getCommands();
    }

    @Override
    public SendMessage process(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String commandName = update.message().text().split(" ")[0];
            Command command = commandMap.get(commandName);
            if (command != null && command.supports(update)) {
                return command.handle(update);
            }
            return new SendMessage(update.message().chat().id(), "Unknown command");
        }
        return null;
    }

}
