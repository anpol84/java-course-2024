package edu.java.service.updater;

import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.utils.LinkUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UpdateService {
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;
    private final static int COUNT_UPDATES = 5;

    @Transactional
    public int update() {
        List<Link> links = linkRepository.findByOldestUpdates(COUNT_UPDATES);
        int count = 0;
        for (Link link : links) {
            String domain = LinkUtils.extractDomainFromUrl(link.getUrl().toString());
            LinkUpdater linkUpdater = linkHolder.getUpdaterByDomain(domain);
            if (linkUpdater.support(link.getUrl().toString())) {
                count += linkUpdater.process(link);
            }

            linkRepository.setUpdateAt(link.getUrl().toString(), OffsetDateTime.now(ZoneOffset.UTC));
        }
        return count;
    }

    @Transactional
    public void deleteUnusedLinks() {
        linkRepository.deleteUnusedLinks();
    }
}
