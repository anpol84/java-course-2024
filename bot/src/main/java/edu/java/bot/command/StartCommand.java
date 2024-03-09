package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.exception.ApiErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    private final ScrapperWebClient scrapperWebClient;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public SendMessage handle(Update update) {
        try {
            scrapperWebClient.registerChat(update.message().chat().id());
            return new SendMessage(update.message().chat().id(), "Bot has started");
        } catch (ApiErrorException e) {
            return new SendMessage(update.message().chat().id(), e.getErrorResponse().getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "This command registers new user";
    }
}
