package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;

public interface TrackingCommand extends Command {
    @Override
    default boolean supports(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        String[] array = update.message().text().split(" ");
        if (array.length != 2 || !array[0].equals(command())) {
            return false;
        }
        return true;
    }
}
