package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HelpCommand implements Command {
    private CommandHolder commandHolder;

    /*
        Циклическая зависимость
     */
    @Autowired
    public void setCommandHolder(CommandHolder commandHolder) {
        this.commandHolder = commandHolder;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public SendMessage handle(Update update) {
        StringBuilder responseText = new StringBuilder("Available commands:\n");
        for (Command command : commandHolder.getCommands()) {
            responseText.append(command.command()).append(" - ").append(command.getDescription()).append("\n");
        }
        return new SendMessage(update.message().chat().id(), responseText.toString());
    }

    @Override
    public String getDescription() {
        return "This command returns list of commands";
    }
}
