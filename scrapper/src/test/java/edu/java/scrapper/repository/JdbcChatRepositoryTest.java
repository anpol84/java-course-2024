package edu.java.scrapper.repository;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Optional;


public class JdbcChatRepositoryTest extends IntegrationTest {

    private final JdbcLinkRepository linkRepository = new JdbcLinkRepository(jdbcTemplate);
    private final JdbcChatRepository chatRepository = new JdbcChatRepository(jdbcTemplate);

    @Test
    void addTest() {
        chatRepository.add(1L);
        List<Chat> chats = chatRepository.findAll();
        assertEquals(1, chats.size());
        assertEquals(1, chats.get(0).getId());
        chatRepository.remove(1L);
    }

    @Test
    void removeTest() {
        chatRepository.add(2L);
        chatRepository.remove(2L);
        List<Chat> chats = chatRepository.findAll();
        assertEquals(0, chats.size());

        chatRepository.add(3L);
        chatRepository.add(4L);
        linkRepository.add(3L, "http://example.ru");
        linkRepository.add(3L, "http://example2.ru");
        linkRepository.add(4L, "http://example2.ru");
        chatRepository.remove(3L);
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());
        assertEquals("http://example2.ru", links.get(0).getUrl());

        chatRepository.remove(4L);
    }

    @Test
    void findAllTest() {
        chatRepository.add(5L);
        chatRepository.add(6L);
        List<Chat> chats = chatRepository.findAll();
        assertEquals(2, chats.size());
        assertEquals(5, chats.get(0).getId());
        assertEquals(6, chats.get(1).getId());
        chatRepository.remove(5L);
        chatRepository.remove(6L);
    }

    @Test
    void findByIdTest() {
        chatRepository.add(7L);
        chatRepository.add(8L);
        Optional<Chat> chatOptional = chatRepository.findById(7L);
        assertTrue(chatOptional.isPresent());
        Chat chat = chatOptional.get();
        assertEquals(7, chat.getId());
        chatRepository.remove(7L);
        chatRepository.remove(8L);
    }
}
