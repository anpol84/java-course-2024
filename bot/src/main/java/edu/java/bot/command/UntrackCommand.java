package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.clientDto.RemoveLinkRequest;
import edu.java.bot.exception.ApiErrorException;
import edu.java.bot.utils.UrlUtils;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final ScrapperWebClient scrapperWebClient;

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public SendMessage handle(Update update) {
        String message = update.message().text().split(" ")[1];
        if (!UrlUtils.isValidUrl(message)) {
            return new SendMessage(update.message().chat().id(), "It is not valid link");
        }
        try {
            scrapperWebClient.removeLink(update.message().chat().id(), new RemoveLinkRequest(new URI(message)));
        } catch (ApiErrorException e) {
            return new SendMessage(update.message().chat().id(), e.getErrorResponse().getDescription());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new SendMessage(update.message().chat().id(), "The resource has been deleted");
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
        return "This command untracks some link";
    }
}
