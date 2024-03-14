package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.repository.ChatRepository;
import edu.java.repository.jooq.JooqChatRepository;
import edu.java.service.ChatService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ChatServiceTest {
    @Test
    public void correctAddTest(){
        ChatRepository chatRepository = mock(JooqChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        chatService.register(1L);
        verify(chatRepository).add(any());
    }

    @Test
    public void badAddTest(){
        ChatRepository chatRepository = mock(JooqChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        doAnswer(invocation -> {
            throw new BadRequestException("The chat is already registered",
                "It is not possible to re-register the chat");
        }).when(chatRepository).add(any());
        try {
            chatService.register(1L);
        }catch (BadRequestException e){
            assertEquals("The chat is already registered", e.getMessage());
            assertEquals("It is not possible to re-register the chat", e.getDescription());
        }
    }

    @Test
    public void correctRemoveTest(){
        ChatRepository chatRepository = mock(JooqChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        when(chatRepository.remove(1L)).thenReturn(1);
        chatService.unregister(1L);
        verify(chatRepository).remove(1L);
    }

    @Test
    public void badRemoveTest(){
        ChatRepository chatRepository = mock(JooqChatRepository.class);
        ChatService chatService = new ChatService(chatRepository);
        try {
            chatService.unregister(1L);
        }catch (NotFoundException e){
            assertEquals("There is no such chat", e.getMessage());
            assertEquals("Deleting a non-existent chat is not possible", e.getDescription());
        }
    }
}
