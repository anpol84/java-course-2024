package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperWebClient;
import edu.java.bot.clientDto.LinkResponse;
import edu.java.bot.clientDto.ListLinksResponse;
import edu.java.bot.exception.ApiErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final ScrapperWebClient scrapperWebClient;

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        String noLinks = "At the moment, no links are being tracked.";
        try {
            ListLinksResponse response = scrapperWebClient.getLinks(chatId).get();
            if (response.getSize() == 0) {
                return new SendMessage(chatId, noLinks);
            }
            StringBuilder message = new StringBuilder("Here is the list of domains and resources:\n");
            for (LinkResponse linkResponse : response.getLinks()) {
                message.append(linkResponse.getUrl()).append(":\n");
            }
            return new SendMessage(chatId, message.toString());
        } catch (ApiErrorException e) {
            return new SendMessage(chatId, e.getErrorResponse().getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "This command returns list of tracks";
    }
}
