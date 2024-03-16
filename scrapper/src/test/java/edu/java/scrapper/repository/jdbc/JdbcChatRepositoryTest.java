package edu.java.scrapper.repository.jdbc;


import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Optional;


public class JdbcChatRepositoryTest extends IntegrationTest {

    private final JdbcLinkRepository linkRepository;
    private final JdbcChatRepository chatRepository;


    public JdbcChatRepositoryTest() {
        this.linkRepository = new JdbcLinkRepository(jdbcTemplate);
        this.chatRepository = new JdbcChatRepository(jdbcTemplate);
    }

    @Test
    void addTest() {
        chatRepository.add(new Chat().setId(1L));
        List<Chat> chats = chatRepository.findAll();
        assertEquals(1, chats.size());
        assertEquals(1, chats.get(0).getId());
        assertThrows(DuplicateKeyException.class, () -> {
            chatRepository.add(new Chat().setId(1L));
        });
        chatRepository.remove(1L);
    }

    @Test
    void removeTest() {
        chatRepository.add(new Chat().setId(2L));
        chatRepository.remove(2L);
        List<Chat> chats = chatRepository.findAll();
        assertEquals(0, chats.size());
        chatRepository.add(new Chat().setId(3L));
        chatRepository.add(new Chat().setId(4L));
        Link link = linkRepository.getOrCreate(new Link().setUrl("http://example.ru"));
        linkRepository.insert(new Chat().setId(3L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl("http://example2.ru"));
        linkRepository.insert(new Chat().setId(3L), link2);
        Link link3 = linkRepository.getOrCreate(new Link().setUrl("http://example2.ru"));
        linkRepository.insert(new Chat().setId(4L), link3);
        chatRepository.remove(3L);
        List<Link> links = linkRepository.findAll();
        List<Chat> chatList = chatRepository.findAll();
        assertEquals(2, links.size());
        assertEquals(1, chatList.size());

        linkRepository.deleteUnusedLinks();
        linkRepository.remove(4L,"http://example2.ru");
        chatRepository.remove(4L);
    }

    @Test
    void findAllTest() {
        chatRepository.add(new Chat().setId(5L));
        chatRepository.add(new Chat().setId(6L));
        List<Chat> chats = chatRepository.findAll();
        assertEquals(2, chats.size());
        assertEquals(5, chats.get(0).getId());
        assertEquals(6, chats.get(1).getId());
        chatRepository.remove(5L);
        chatRepository.remove(6L);
    }

    @Test
    void findByIdTest() {
        chatRepository.add(new Chat().setId(7L));
        chatRepository.add(new Chat().setId(8L));
        Optional<Chat> chatOptional = chatRepository.findById(7L);
        assertTrue(chatOptional.isPresent());
        Chat chat = chatOptional.get();
        assertEquals(7, chat.getId());
        chatRepository.remove(7L);
        chatRepository.remove(8L);
    }
}
