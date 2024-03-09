package edu.java.service.updater;

import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.utils.LinkUtils;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UpdateService {
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;
    private final static int COUNT_UPDATES = 5;

    public int update() {
        List<Link> links = linkRepository.findByOldestUpdates(COUNT_UPDATES);
        int count = 0;
        for (Link link : links) {
            String domain = LinkUtils.extractDomainFromUrl(link.getUrl());
            LinkUpdater linkUpdater = linkHolder.getUpdaterByDomain(domain);
            if (linkUpdater.support(link.getUrl())) {
                count += linkUpdater.process(link);
            }

            linkRepository.setUpdateAt(link.getUrl(), OffsetDateTime.now(ZoneOffset.UTC));
        }
        return count;
    }

    public void deleteUnusedLinks() {
        linkRepository.deleteUnusedLinks();
    }
}
