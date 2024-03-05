package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public void register(Long tgChatId) {
        try {
            chatRepository.add(tgChatId);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Чат уже зарегистрирован", "Повторная регистрация чата невозможна");
        }
    }

    public void unregister(Long tgChatId) {
        int count = chatRepository.remove(tgChatId);
        if (count == 0) {
            throw new NotFoundException("Такого чата не существует", "Удаление несуществующего чата невозможно");
        }
    }
}
