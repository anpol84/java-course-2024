package edu.java.bot.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommandHolder {
    private Map<String, Command> commandMap;

    @Autowired
    public void setCommandMap(List<Command> commands) {
        commandMap = new HashMap<>();
        for (Command command : commands) {
            commandMap.put(command.command(), command);
        }
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commandMap.values());
    }

    public Command getCommandByName(String commandName) {
        return commandMap.get(commandName);
    }
}
