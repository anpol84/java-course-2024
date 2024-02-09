package edu.java.bot.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.command.CommandsList;
import edu.java.bot.handler.MessageProcessor;
import edu.java.bot.handler.TelegramBotMessageProcessor;
import java.util.List;


public class BotImpl implements Bot {
    private final TelegramBot bot;
    private final MessageProcessor messageProcessor;

    public BotImpl(String token) {
        this.bot = new TelegramBot(token);
        this.messageProcessor = new TelegramBotMessageProcessor();
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
        bot.execute(new SetMyCommands(CommandsList.mapToBotCommands().toArray(new BotCommand[0])));
        bot.setUpdatesListener(updates -> {
            process(updates);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @Override
    public void close() {
        bot.removeGetUpdatesListener();
    }

}
