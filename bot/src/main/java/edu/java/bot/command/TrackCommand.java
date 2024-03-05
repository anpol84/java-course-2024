package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.exception.ApiErrorException;
import edu.java.bot.exception.NotValidLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final ScrapperWebClient scrapperWebClient;

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public SendMessage handle(Update update) {
        try {
            scrapperWebClient.addLink(update.message());
        } catch (ApiErrorException e) {
            return new SendMessage(update.message().chat().id(), e.getErrorResponse().getDescription());
        } catch (NotValidLinkException e) {
            return new SendMessage(update.message().chat().id(), e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SendMessage(update.message().chat().id(), "The resource has been added");
    }

    @Override
    public boolean supports(Update update) {
        if (update.message() == null || update.message().text() == null) {
            return false;
        }
        String[] array = update.message().text().split(" ");
        if (array.length != 2 || !array[0].equals(command())) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "This command tracks some link";
    }
}
