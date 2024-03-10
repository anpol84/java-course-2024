package edu.java.service;

import edu.java.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository jooqChatRepository;

    @Transactional
    public void register(Long tgChatId) {
        jooqChatRepository.add(tgChatId);
    }

    @Transactional
    public void unregister(Long tgChatId) {
        int count = jooqChatRepository.remove(tgChatId);
    }
}
