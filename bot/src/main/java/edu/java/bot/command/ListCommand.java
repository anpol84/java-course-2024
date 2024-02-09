package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.LinkDao;
import java.util.Map;
import java.util.Set;


public class ListCommand implements Command {
    private final LinkDao linkDao;

    public ListCommand(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.message().chat().id();
        String noLinks = "At the moment, no links are being tracked.";
        Map<String, Set<String>> domainsAndResources = linkDao.getResources().get(chatId);
        if (domainsAndResources == null) {
            return new SendMessage(chatId, noLinks);
        }
        StringBuilder message = new StringBuilder("Here is the list of domains and resources:\n");
        boolean isNotEmptyDomains = false;
        for (Map.Entry<String, Set<String>> entry : domainsAndResources.entrySet()) {
            message.append(entry.getKey()).append(":\n");
            for (String resource : entry.getValue()) {
                message.append(entry.getKey()).append("/").append(resource).append("\n");
                isNotEmptyDomains = true;
            }
        }
        if (!isNotEmptyDomains) {
            return new SendMessage(chatId, noLinks);

        }
        return new SendMessage(chatId, message.toString());
    }

    @Override
    public String getDescription() {
        return "This command returns list of tracks";
    }
}
