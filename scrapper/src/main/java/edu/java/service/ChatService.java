package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository jooqChatRepository;

    @Transactional
    public void register(Long tgChatId) {
        try {
            jooqChatRepository.add(new Chat().setId(tgChatId));
        } catch (DataIntegrityViolationException | IntegrityConstraintViolationException e) {
            throw new BadRequestException("The chat is already registered",
                "It is not possible to re-register the chat");
        }
    }

    @Transactional
    public void unregister(Long tgChatId) {
        int count = jooqChatRepository.remove(tgChatId);
        if (count == 0) {
            throw new NotFoundException("There is no such chat", "Deleting a non-existent chat is not possible");
        }
    }
}
