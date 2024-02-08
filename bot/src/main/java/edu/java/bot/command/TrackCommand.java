package edu.java.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.LinkDao;
import edu.java.bot.utils.UrlUtils;

public class TrackCommand implements TrackingCommand {
    private final LinkDao linkDao;

    public TrackCommand(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    @Override
    public String command() {
        return "/track";
    }

    @Override
    public SendMessage handle(Update update) {
        String message = update.message().text().split(" ")[1];
        if (!UrlUtils.isValidUrl(message)) {
            return new SendMessage(update.message().chat().id(), "It is not valid link");
        }
        linkDao.addResource(UrlUtils.getDomain(message), UrlUtils.getPath(message));
        return new SendMessage(update.message().chat().id(), "The resource has been added");

    }

    @Override
    public String getDescription() {
        return "This command tracks some link";
    }


}
