package edu.java.scrapper.service.updater;

import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.service.updater.GithubLinkUpdater;
import edu.java.service.updater.LinkHolder;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.UpdateService;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UpdateServiceTest {
    @Test
    public void processTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkUpdater linkUpdater = mock(GithubLinkUpdater.class);
        when(linkRepository.findByOldestUpdates(5))
            .thenReturn(List.of(new Link().setId(1L).setUrl(URI.create("1")).setUpdateAt(OffsetDateTime.MAX)
                    .setLastApiUpdate(OffsetDateTime.MAX.minusDays(2)),
                new Link().setId(2L).setUrl(URI.create("2")).setUpdateAt(OffsetDateTime.MAX)
                    .setLastApiUpdate(OffsetDateTime.MAX.minusDays(2)),
                new Link().setId(3L).setUrl(URI.create("3")).setUpdateAt(OffsetDateTime.MAX)
                    .setLastApiUpdate(OffsetDateTime.MAX.minusDays(2)),
                new Link().setId(4L).setUrl(URI.create("4")).setUpdateAt(OffsetDateTime.MAX)
                    .setLastApiUpdate(OffsetDateTime.MAX.minusDays(2)),
                new Link().setId(5L).setUrl(URI.create("5")).setUpdateAt(OffsetDateTime.MAX)
                    .setLastApiUpdate(OffsetDateTime.MAX.minusDays(2))));
        when(linkHolder.getUpdaterByDomain(any())).thenReturn(linkUpdater);
        when(linkUpdater.support(any())).thenReturn(true);
        when(linkUpdater.process(any())).thenReturn(1);
        UpdateService updateService = new UpdateService(linkRepository, linkHolder);
        int count = updateService.update();
        assertEquals(5, count);
    }
}
