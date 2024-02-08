package edu.java.bot.handler;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.dao.LinkDao;
import java.util.ArrayList;
import java.util.List;

public class TelegramBotMessageProcessor implements MessageProcessor {

    private final List<Command> commands;

    public TelegramBotMessageProcessor() {
        this.commands = new ArrayList<>();
        LinkDao linkDao = new LinkDao();
        commands.add(new HelpCommand(commands));
        commands.add(new StartCommand());
        commands.add(new ListCommand(linkDao));
        commands.add(new TrackCommand(linkDao));
        commands.add(new UntrackCommand(linkDao));
    }

    @Override
    public List<? extends Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        if (update.message() != null && update.message().text() != null) {
            for (Command command : commands) {
                if (command.supports(update)) {
                    return command.handle(update);
                }
            }
            return new SendMessage(update.message().chat().id(), "Unknown command");
        }
        return null;
    }


}
