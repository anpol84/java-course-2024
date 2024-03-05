package edu.java.scrapper.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.GithubWebClient;
import edu.java.clientDto.GithubResponse;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.updater.GithubLinkUpdater;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GithubLinkUpdaterTest {

    GithubWebClient githubWebClient = mock(GithubWebClient.class);
    LinkRepository linkRepository = mock(JdbcLinkRepository.class);
    BotWebClient botWebClient = mock(BotWebClient.class);

    @Test
    public void processTest(){
        Link link = new Link(1L, "https://github.com/some/some", OffsetDateTime.MAX,
            OffsetDateTime.MAX.minusDays(2));
        GithubWebClient githubWebClient = mock(GithubWebClient.class);
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        when(githubWebClient.fetchLatestRepositoryActivity("some",
            "some")).thenReturn(new GithubResponse(1L, "1", null,
            null,OffsetDateTime.MAX));
        when(linkRepository.findChatIdsByUrl(link.getUrl())).thenReturn(List.of(1L));
        when(botWebClient.sendUpdate(any())).thenReturn("Обновление обработано");
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        int count = githubLinkUpdater.process(link);
        assertEquals(1, count);
    }

    @Test
    public void processFirstTimeTest(){
        Link link = new Link(1L, "https://github.com/some/some", OffsetDateTime.MAX,
            OffsetDateTime.MIN);
        GithubWebClient githubWebClient = mock(GithubWebClient.class);
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        when(githubWebClient.fetchLatestRepositoryActivity("some",
            "some")).thenReturn(new GithubResponse(1L, "1", null,
            null,OffsetDateTime.MAX));
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        int count = githubLinkUpdater.process(link);
        assertEquals(0, count);
    }


    @Test
    public void noProcessTest(){
        Link link = new Link(1L, "https://github.com/some/some", OffsetDateTime.MAX, OffsetDateTime.MAX);
        GithubWebClient githubWebClient = mock(GithubWebClient.class);
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        when(githubWebClient.fetchLatestRepositoryActivity("some",
            "some")).thenReturn(new GithubResponse(1L, "1", null,
            null,OffsetDateTime.MIN));
        when(linkRepository.findChatIdsByUrl(link.getUrl())).thenReturn(List.of(1L));
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        int count = githubLinkUpdater.process(link);
        assertEquals(0, count);
    }

    @Test
    public void supportTest(){
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        boolean flag1 = githubLinkUpdater.support("123");
        assertFalse(flag1);
        boolean flag2 = githubLinkUpdater.support("https://github.com/some/some");
        assertTrue(flag2);
    }

    @Test
    public void processLinkTest(){
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        String[] args = githubLinkUpdater.processLink("https://github.com/anpol84/library");
        assertEquals(2, args.length);
        assertEquals("library", args[0]);
        assertEquals("anpol84", args[1]);
    }

    @Test
    public void getDomainTest(){
        GithubLinkUpdater githubLinkUpdater = new GithubLinkUpdater(githubWebClient,linkRepository,botWebClient);
        assertEquals("github.com", githubLinkUpdater.getDomain());
    }
}
