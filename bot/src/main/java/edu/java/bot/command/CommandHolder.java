package edu.java.bot.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CommandHolder {
    private Map<String, Command> commandMap;

    @Autowired
    public void setCommandMap(List<Command> commands) {
       commandMap = commands.stream().collect(Collectors.toMap(Command::command, Function.identity()));
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commandMap.values());
    }

    public Command getCommandByName(String commandName) {
        return commandMap.get(commandName);
    }
}
