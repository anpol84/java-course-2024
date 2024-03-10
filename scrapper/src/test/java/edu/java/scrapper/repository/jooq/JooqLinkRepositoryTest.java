package edu.java.scrapper.repository.jooq;

import edu.java.model.Link;
import edu.java.repository.jooq.JooqChatRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class JooqLinkRepositoryTest extends IntegrationTest {
    private final JooqLinkRepository linkRepository = new JooqLinkRepository(dslContext);
    private final JooqChatRepository chatRepository = new JooqChatRepository(dslContext);

    @Test
    void addTest() {
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();

    }

    @Test
    void removeTest() {
        chatRepository.add(2L);
        linkRepository.add(2L, "http://test.ru");
        linkRepository.remove(2L, "http://test.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());

        chatRepository.add(3L);
        linkRepository.add(2L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        linkRepository.add(3L, "http://test2.ru");
        linkRepository.remove(2L, "http://test.ru");
        linkRepository.remove(2L, "http://test2.ru");
        List<Link> links2 = linkRepository.findAll();
        assertEquals(2, links2.size());
        assertEquals("http://test.ru", links2.get(0).getUrl().toString());

        linkRepository.remove(3L, "http://test2.ru");
        chatRepository.remove(2L);
        chatRepository.remove(3L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void findAllByChatIdTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        List<Link> links = linkRepository.findAllByChatId(1L);
        assertEquals(1, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(2L, "http://test2.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void findAllTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(2, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl().toString());
        assertEquals("http://test2.ru", links.get(1).getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(2L, "http://test2.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();

    }

    @Test
    void findByChatIdAndUrlTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(1L, "http://test3.ru");
        linkRepository.add(2L, "http://test2.ru");
        Link link = linkRepository.findByChatIdAndUrl(1L, "http://test3.ru");
        assertEquals("http://test3.ru", link.getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(1L, "http://test3.ru");
        linkRepository.remove(2L, "http://test2.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void findByLastActivityTest(){
        chatRepository.add(1L);
        chatRepository.add(2L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(2L, "http://test2.ru");
        linkRepository.add(1L, "http://test3.ru");
        List<Link> links = linkRepository.findByOldestUpdates(2);
        assertEquals(2, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl().toString());
        assertEquals("http://test2.ru", links.get(1).getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(2L, "http://test2.ru");
        linkRepository.remove(1L, "http://test3.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void setUpdateAtTest(){
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.setUpdateAt("http://test.ru", OffsetDateTime.now());
        Link link = linkRepository.findByChatIdAndUrl(1L, "http://test.ru");
        assertEquals(OffsetDateTime.now().getHour(), link.getUpdateAt().getHour());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void setLastApiUpdateTest(){
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        OffsetDateTime now = OffsetDateTime.now();
        linkRepository.setLastApiUpdate("http://test.ru", now);
        Link link = linkRepository.findByChatIdAndUrl(1L, "http://test.ru");
        assertEquals(now.getHour(), link.getLastApiUpdate().getHour());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
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
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(2L, "http://test.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void deleteUnusedLinksTest(){
        chatRepository.add(1L);
        linkRepository.add(1L, "http://test.ru");
        linkRepository.add(1L, "http://test2.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
        List<Link> links = linkRepository.findAll();
        assertEquals(0, links.size());
    }
}
