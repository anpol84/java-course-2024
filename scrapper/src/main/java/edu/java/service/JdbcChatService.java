package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.repository.JdbcChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository jdbcChatRepository;

    @Override
    public void register(Long tgChatId) {
        if (jdbcChatRepository.findById(tgChatId) != null) {
            throw new BadRequestException("Чат уже зарегистрирован", "Повторная регистрация чата невозможна");
        }
        jdbcChatRepository.add(tgChatId);
    }

    @Override
    public void unregister(Long tgChatId) {
        if (jdbcChatRepository.findById(tgChatId) == null) {
            throw new NotFoundException("Такого чата не существует", "Удаление несуществующего чата невозможно");
        }
        jdbcChatRepository.remove(tgChatId);
    }
}
