package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import edu.java.bot.dao.LinkDao;
import java.util.ArrayList;
import java.util.List;


public class CommandsList {
    private static final List<Command> COMMANDS;

    static {
        COMMANDS = new ArrayList<>();
        LinkDao linkDao = new LinkDao();
        COMMANDS.add(new HelpCommand());
        COMMANDS.add(new StartCommand());
        COMMANDS.add(new ListCommand(linkDao));
        COMMANDS.add(new TrackCommand(linkDao));
        COMMANDS.add(new UntrackCommand(linkDao));
    }

    private CommandsList() {

    }

    public static List<? extends Command> getCommands() {
        return COMMANDS;
    }

    public static List<BotCommand> mapToBotCommands() {
        List<BotCommand> botCommands = new ArrayList<>();
        for (Command command : COMMANDS) {
            botCommands.add(new BotCommand(command.command(), command.getDescription()));
        }
        return botCommands;
    }

}
