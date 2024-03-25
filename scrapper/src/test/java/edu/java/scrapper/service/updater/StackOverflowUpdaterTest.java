package edu.java.scrapper.service.updater;

import edu.java.client.BotWebClient;
import edu.java.client.StackOverflowWebClient;
import edu.java.clientDto.StackOverflowResponse;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.service.updater.StackOverflowLinkUpdater;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class StackOverflowUpdaterTest {

    StackOverflowWebClient stackOverflowWebClient = mock(StackOverflowWebClient.class);
    LinkRepository linkRepository = mock(JooqLinkRepository.class);
    BotWebClient botWebClient = mock(BotWebClient.class);

    @Test
    public void processTest(){
        Link link = new Link().setId(1L).setUrl(URI.create("https://stackoverflow.com/questions/123"))
            .setUpdateAt(OffsetDateTime.MIN).setLastApiUpdate(OffsetDateTime.MAX.minusDays(2));
        StackOverflowWebClient stackOverflowWebClient = mock(StackOverflowWebClient.class);
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        when(stackOverflowWebClient.fetchLatestAnswerWithRetry(123L))
            .thenReturn(new StackOverflowResponse().setAnswerId(1L).setQuestionId(1L)
                .setLastActivityDate(OffsetDateTime.MAX));
        when(linkRepository.findChatIdsByUrl(link.getUrl().toString())).thenReturn(List.of(1L));
        when(botWebClient.sendUpdate(any())).thenReturn("Обновление обработано");
        StackOverflowLinkUpdater stackOverflowLinkUpdater =
            new StackOverflowLinkUpdater(linkRepository, stackOverflowWebClient,botWebClient);
        int count = stackOverflowLinkUpdater.process(link);
        assertEquals(1, count);
    }

    @Test
    public void noProcessTest(){
        Link link = new Link().setId(1L).setUrl(URI.create("https://stackoverflow.com/questions/123"))
            .setUpdateAt(OffsetDateTime.MAX).setLastApiUpdate(OffsetDateTime.MAX);
        StackOverflowWebClient stackOverflowWebClient = mock(StackOverflowWebClient.class);
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        BotWebClient botWebClient = mock(BotWebClient.class);
        when(stackOverflowWebClient.fetchLatestAnswerWithRetry(123L))
            .thenReturn(new StackOverflowResponse().setAnswerId(1L).setQuestionId(1L)
                .setLastActivityDate(OffsetDateTime.MIN));
        when(linkRepository.findChatIdsByUrl(link.getUrl().toString())).thenReturn(List.of(1L));
        StackOverflowLinkUpdater stackOverflowLinkUpdater =
            new StackOverflowLinkUpdater(linkRepository, stackOverflowWebClient,botWebClient);
        int count = stackOverflowLinkUpdater.process(link);
        assertEquals(0, count);
    }

    @Test
    public void supportTest(){
        StackOverflowLinkUpdater stackOverflowLinkUpdater =
            new StackOverflowLinkUpdater(linkRepository, stackOverflowWebClient,botWebClient);
        boolean flag1 = stackOverflowLinkUpdater.support("123");
        assertFalse(flag1);
        boolean flag2 = stackOverflowLinkUpdater.support("https://stackoverflow.com/questions/123");
        assertTrue(flag2);
    }

    @Test
    public void getDomainTest(){
        StackOverflowLinkUpdater stackOverflowLinkUpdater =
            new StackOverflowLinkUpdater(linkRepository, stackOverflowWebClient,botWebClient);
        assertEquals("stackoverflow.com", stackOverflowLinkUpdater.getDomain());
    }
}
