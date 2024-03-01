package edu.java.scrapper.repository;

import edu.java.model.Link;
import edu.java.repository.JdbcChatRepository;
import edu.java.repository.JdbcLinkRepository;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.OffsetDateTime;
import java.util.List;


public class JdbcLinkRepositoryTest extends IntegrationTest {
    private final JdbcLinkRepository linkRepository = new JdbcLinkRepository(jdbcTemplate);
    private final JdbcChatRepository chatRepository = new JdbcChatRepository(jdbcTemplate, linkRepository);

    @Test
    void addTest() {
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl());
        chatRepository.remove(1L);
    }

    @Test
    void removeTest() {
        chatRepository.add(2L);
        linkRepository.add(2L, "http://test.ru");
        linkRepository.remove(2L, "http://test.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(0, links.size());

        chatRepository.add(3L);
        linkRepository.add(2L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        linkRepository.add(3L, "http://test2.ru");
        linkRepository.remove(2L, "http://test.ru");
        linkRepository.remove(2L, "http://test2.ru");
        List<Link> links2 = linkRepository.findAll();
        assertEquals(1, links2.size());
        assertEquals("http://test2.ru", links2.get(0).getUrl());

        chatRepository.remove(2L);
        chatRepository.remove(3L);
    }

    @Test
    void findAllByChatIdTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        List<Link> links = linkRepository.findAllByChatId(1L);
        assertEquals(1, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl());
        chatRepository.remove(1L);
        chatRepository.remove(2L);
    }

    @Test
    void findAllTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(2, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl());
        assertEquals("http://test2.ru", links.get(1).getUrl());
        chatRepository.remove(1L);
        chatRepository.remove(2L);
    }

    @Test
    void findByChatIdAndUrlTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(1L, "http://test3.ru");
        linkRepository.add(2L, "http://test2.ru");
        Link link = linkRepository.findByChatIdAndUrl(1L, "http://test3.ru");
        assertEquals("http://test3.ru", link.getUrl());
        chatRepository.remove(1L);
        chatRepository.remove(2L);
    }

    @Test
    void findByLastActivityTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        linkRepository.add(1L, "http://test3.ru");
        List<Link> links = linkRepository.findByLastActivity(2);
        assertEquals(2, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl());
        assertEquals("http://test2.ru", links.get(1).getUrl());
        chatRepository.remove(1L);
        chatRepository.remove(2L);
    }

    @Test
    void updateTimeTest(){
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.updateTime("http://test.ru", OffsetDateTime.MAX);
        Link link = linkRepository.findByChatIdAndUrl(1L, "http://test.ru");
        assertEquals(OffsetDateTime.MAX, link.getLastActivity());
        chatRepository.remove(1L);
    }

    @Test
    void findChatIdsByUrlTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test.ru");
        List<Long> ids = linkRepository.findChatIdsByUrl("http://test.ru");
        assertEquals(2, ids.size());
        assertEquals(1, ids.get(0));
        assertEquals(2, ids.get(1));
        chatRepository.remove(1L);
        chatRepository.remove(2L);
    }
}
