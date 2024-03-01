package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.repository.JdbcChatRepository;
import edu.java.service.JdbcChatService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class JdbcChatServiceTest {
    @Test
    public void correctAddTest(){
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(null);
        JdbcChatService jdbcChatService = new JdbcChatService(jdbcChatRepository);
        jdbcChatService.register(1L);
        verify(jdbcChatRepository).findById(1L);
        verify(jdbcChatRepository).add(1L);
    }

    @Test
    public void badAddTest(){
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        JdbcChatService jdbcChatService = new JdbcChatService(jdbcChatRepository);
        try {
            jdbcChatService.register(1L);
        }catch (BadRequestException e){
            assertEquals("Чат уже зарегистрирован", e.getMessage());
            assertEquals("Повторная регистрация чата невозможна", e.getDescription());
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcChatRepository, never()).add(anyLong());
        }
    }

    @Test
    public void correctRemoveTest(){
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        JdbcChatService jdbcChatService = new JdbcChatService(jdbcChatRepository);
        jdbcChatService.unregister(1L);
        verify(jdbcChatRepository).findById(1L);
        verify(jdbcChatRepository).remove(1L);
    }

    @Test
    public void badRemoveTest(){
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(null);
        JdbcChatService jdbcChatService = new JdbcChatService(jdbcChatRepository);
        try {
            jdbcChatService.unregister(1L);
        }catch (NotFoundException e){
            assertEquals("Такого чата не существует", e.getMessage());
            assertEquals("Удаление несуществующего чата невозможно", e.getDescription());
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcChatRepository, never()).remove(anyLong());
        }
    }
}
