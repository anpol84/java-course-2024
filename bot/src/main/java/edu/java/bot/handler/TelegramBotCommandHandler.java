package edu.java.bot.handler;

import edu.java.bot.model.BotImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TelegramBotCommandHandler {

    private final BotImpl bot;

    public void startBot() {
        bot.start();
    }

    @PostConstruct
    public void init() {
        startBot();
    }
}
