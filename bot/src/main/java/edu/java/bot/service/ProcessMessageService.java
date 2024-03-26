package edu.java.bot.service;

import edu.java.bot.model.BotImpl;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessMessageService {

    private final BotImpl bot;

    public void process(LinkUpdateRequest linkUpdate) {
        for (Long chat : linkUpdate.getTgChatIds()) {
            bot.sendMessageToChat(String.valueOf(chat), linkUpdate.getDescription());
        }
    }


}
