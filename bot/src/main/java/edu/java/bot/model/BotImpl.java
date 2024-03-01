package edu.java.bot.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandHolder;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.handler.MessageProcessor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class BotImpl implements Bot {
    private final TelegramBot bot;
    private final MessageProcessor messageProcessor;
    private final CommandHolder commandHolder;
    private final ApplicationConfig applicationConfig;

    public BotImpl(MessageProcessor messageProcessor, CommandHolder commandHolder,
        ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.messageProcessor = messageProcessor;
        this.commandHolder = commandHolder;
        this.bot = new TelegramBot(this.applicationConfig.telegramToken());
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        bot.execute(request);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            SendMessage response = messageProcessor.process(update);
            if (response != null) {
                bot.execute(response);
            }
        }
        return updates.get(updates.size() - 1).updateId();
    }

    @Override
    public void start() {
        bot.execute(new SetMyCommands(mapToBotCommands(commandHolder.getCommands()).toArray(new BotCommand[0])));
        bot.setUpdatesListener(updates -> {
            process(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @Override
    public void close() {
        bot.removeGetUpdatesListener();
    }

    public static List<BotCommand> mapToBotCommands(List<Command> commands) {
        List<BotCommand> botCommands = new ArrayList<>();
        for (Command command : commands) {
            botCommands.add(new BotCommand(command.command(), command.getDescription()));
        }
        return botCommands;
    }

    public void sendMessageToChat(String chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        bot.execute(sendMessage);
    }
}
