package edu.java.bot.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommandHolder {
    private final List<Command> commands;
    private final Map<String, Command> commandMap;
    private HelpCommand helpCommand;

    /*
    Тут использую инъекцию через сеттер, т.к. образуется циклическая зависимость (как по другому ее избежать
    не придумал, но вроде бы она не сильно критичная, да и была с самого начала, просто до этого я не создавал
    бины
    */
    @Autowired
    public void setHelpCommand(HelpCommand helpCommand) {
        this.helpCommand = helpCommand;
        commands.add(helpCommand);
        commandMap.put(helpCommand.command(), helpCommand);
    }

    public CommandHolder(
        StartCommand startCommand,
        ListCommand listCommand,
        TrackCommand trackCommand,
        UntrackCommand untrackCommand
    ) {
        commands = new ArrayList<>();
        commands.add(startCommand);
        commands.add(listCommand);
        commands.add(trackCommand);
        commands.add(untrackCommand);
        commandMap = new HashMap<>();
        for (Command command : commands) {
            commandMap.put(command.command(), command);
        }
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Command getCommandByName(String commandName) {
        return commandMap.get(commandName);
    }
}
