package edu.java.bot.command;


import edu.java.bot.dao.LinkDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHolder {
    private static final List<Command> COMMANDS;
    private static final Map<String, Command> COMMAND_MAP;

    static {
        COMMANDS = new ArrayList<>();
        LinkDao linkDao = new LinkDao();
        COMMANDS.add(new HelpCommand());
        COMMANDS.add(new StartCommand());
        COMMANDS.add(new ListCommand(linkDao));
        COMMANDS.add(new TrackCommand(linkDao));
        COMMANDS.add(new UntrackCommand(linkDao));

        COMMAND_MAP = new HashMap<>();
        for (Command command : COMMANDS) {
            COMMAND_MAP.put(command.command(), command);
        }
    }

    private CommandHolder() {

    }

    public static List<Command> getCommands() {
        return COMMANDS;
    }


    public static Command getCommandByName(String commandName) {
        return COMMAND_MAP.get(commandName);
    }

}
