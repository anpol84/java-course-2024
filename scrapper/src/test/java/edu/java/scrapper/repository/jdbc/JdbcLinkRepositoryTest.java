package edu.java.scrapper.repository.jdbc;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;


public class JdbcLinkRepositoryTest extends IntegrationTest {

    private final JdbcLinkRepository linkRepository = new JdbcLinkRepository(jdbcTemplate);
    private final JdbcChatRepository chatRepository = new JdbcChatRepository(jdbcTemplate);

    @Test
    void addTest() {
        chatRepository.add(new Chat().setId(1L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());
        assertEquals("http://test.ru", links.get(0).getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();

    }



    @Test
    void removeTest() {
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(2L), link);
        linkRepository.remove(2L, "http://test.ru");
        List<Link> links = linkRepository.findAll();
        assertEquals(1, links.size());

        chatRepository.add(new Chat().setId(3L));
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
        Link link3 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(2L), link3);
        Link link4 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(3L), link4);
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
        chatRepository.add(new Chat().setId(1L));
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
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
        chatRepository.add(new Chat().setId(1L));
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
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
        chatRepository.add(new Chat().setId(1L));
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
        Link link3 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test3.ru")));
        linkRepository.insert(new Chat().setId(1L), link3);

        Link linkResp = linkRepository.findByChatIdAndUrl(1L, "http://test3.ru");
        assertEquals("http://test3.ru", linkResp.getUrl().toString());
        linkRepository.remove(1L, "http://test.ru");
        linkRepository.remove(1L, "http://test3.ru");
        linkRepository.remove(2L, "http://test2.ru");
        chatRepository.remove(1L);
        chatRepository.remove(2L);
        linkRepository.deleteUnusedLinks();
    }



    @Test
    void findByLastActivityTest(){
        chatRepository.add(new Chat().setId(1L));
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
        Link link3 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test3.ru")));
        linkRepository.insert(new Chat().setId(1L), link3);

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
        chatRepository.add(new Chat().setId(1L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        linkRepository.setUpdateAt("http://test.ru", OffsetDateTime.MAX);
        Link linkResp = linkRepository.findByChatIdAndUrl(1L, "http://test.ru");
        assertEquals(OffsetDateTime.MAX, linkResp.getUpdateAt());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
    }
    @Test
    void setLastApiUpdateTest(){
        chatRepository.add(new Chat().setId(1L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        linkRepository.setLastApiUpdate("http://test.ru", OffsetDateTime.MAX);
        Link linkResp = linkRepository.findByChatIdAndUrl(1L, "http://test.ru");
        assertEquals(OffsetDateTime.MAX, linkResp.getLastApiUpdate());
        linkRepository.remove(1L, "http://test.ru");
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
    }

    @Test
    void findChatIdsByUrlTest(){
        chatRepository.add(new Chat().setId(1L));
        chatRepository.add(new Chat().setId(2L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(2L), link2);
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
        chatRepository.add(new Chat().setId(1L));
        Link link = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test.ru")));
        linkRepository.insert(new Chat().setId(1L), link);
        Link link2 = linkRepository.getOrCreate(new Link().setUrl(URI.create("http://test2.ru")));
        linkRepository.insert(new Chat().setId(1L), link2);
        chatRepository.remove(1L);
        linkRepository.deleteUnusedLinks();
        List<Link> links = linkRepository.findAll();
        assertEquals(0, links.size());
    }
}
