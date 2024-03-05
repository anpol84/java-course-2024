package edu.java.bot.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CommandConfiguration {

    @Bean
    public Map<String, Command> commandMap(HelpCommand helpCommand, StartCommand startCommand,
        ListCommand listCommand, TrackCommand trackCommand, UntrackCommand untrackCommand) {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put(helpCommand.command(), helpCommand);
        commandMap.put(startCommand.command(), startCommand);
        commandMap.put(listCommand.command(), listCommand);
        commandMap.put(trackCommand.command(), trackCommand);
        commandMap.put(untrackCommand.command(), untrackCommand);
        return commandMap;
    }
}
