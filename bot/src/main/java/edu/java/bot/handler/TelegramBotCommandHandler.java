package edu.java.bot.handler;


import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.model.BotImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramBotCommandHandler {



    private final ApplicationConfig applicationConfig;

    public void startBot() {

        BotImpl bot = new BotImpl(applicationConfig.telegramToken());
        bot.start();

    }

    @PostConstruct
    public void init() {
        startBot();
    }
}
