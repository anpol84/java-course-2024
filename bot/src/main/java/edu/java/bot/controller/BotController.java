package edu.java.bot.controller;

import edu.java.bot.model.BotImpl;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class BotController {
    private final BotImpl bot;

    @PostMapping
    public String sendUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdate) {
        for (Long chat : linkUpdate.getTgChatIds()) {
            bot.sendMessageToChat(String.valueOf(chat), linkUpdate.getDescription());
        }
        return "Обновление обработано";
    }
}
