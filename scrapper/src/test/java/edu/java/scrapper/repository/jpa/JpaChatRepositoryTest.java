package edu.java.scrapper.repository.jpa;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaChatRepositoryInterface;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaLinkRepositoryInterface;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaChatRepositoryTest extends IntegrationTest {

    private final JpaLinkRepository linkRepository;

    private final JpaChatRepository chatRepository;

    @Autowired
    public JpaChatRepositoryTest(
        JpaLinkRepositoryInterface jpaLinkRepositoryInterface,
        JpaChatRepositoryInterface jpaChatRepositoryInterface
    ) {
        linkRepository = new JpaLinkRepository(jpaLinkRepositoryInterface);
         chatRepository = new JpaChatRepository(jpaChatRepositoryInterface);
    }



    @Test
    void addTest() {
        chatRepository.add(new Chat().setId(1L));
        List<Chat> chats = chatRepository.findAll();
        assertEquals(1, chats.size());
        assertEquals(1, chats.get(0).getId());
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
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://example.ru")));
        linkRepository.insert(new Chat().setId(3L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://example2.ru")));
        linkRepository.insert(new Chat().setId(3L), link2);
        Link link3 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://example2.ru")));
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
