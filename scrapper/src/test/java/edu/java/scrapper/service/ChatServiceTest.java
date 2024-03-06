package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.repository.ChatRepository;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ChatServiceTest {
    @Test
    public void correctAddTest(){
        ChatRepository chatRepository = mock(JdbcChatRepository.class);
        ChatService jdbcChatService = new ChatService(chatRepository);
        jdbcChatService.register(1L);
        verify(chatRepository).add(1L);
    }

    @Test
    public void badAddTest(){
        ChatRepository chatRepository = mock(JdbcChatRepository.class);
        ChatService jdbcChatService = new ChatService(chatRepository);
        doAnswer(invocation -> {
            throw new DataIntegrityViolationException("some");
        }).when(chatRepository).add(1L);
        try {
            jdbcChatService.register(1L);
        }catch (BadRequestException e){
            assertEquals("Чат уже зарегистрирован", e.getMessage());
            assertEquals("Повторная регистрация чата невозможна", e.getDescription());
        }
    }

    @Test
    public void correctRemoveTest(){
        ChatRepository chatRepository = mock(JdbcChatRepository.class);
        ChatService jdbcChatService = new ChatService(chatRepository);
        when(chatRepository.remove(1L)).thenReturn(1);
        jdbcChatService.unregister(1L);
        verify(chatRepository).remove(1L);
    }

    @Test
    public void badRemoveTest(){
        ChatRepository chatRepository = mock(JdbcChatRepository.class);
        ChatService jdbcChatService = new ChatService(chatRepository);
        try {
            jdbcChatService.unregister(1L);
        }catch (NotFoundException e){
            assertEquals("Такого чата не существует", e.getMessage());
            assertEquals("Удаление несуществующего чата невозможно", e.getDescription());
        }
    }
}
