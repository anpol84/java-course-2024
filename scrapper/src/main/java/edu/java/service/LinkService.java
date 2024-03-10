package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.service.updater.LinkHolder;
import edu.java.service.updater.LinkUpdater;
import edu.java.serviceDto.LinkResponse;
import edu.java.utils.LinkUtils;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository jooqLinkRepository;
    private final LinkHolder linkHolder;

    @Transactional(rollbackFor = Exception.class)
    public LinkResponse add(long tgChatId, URI url) {
        if (!LinkUtils.validateLink(url.toString())) {
            throw new BadRequestException("Bad link", "This link is not supported");
        }
        Link link = jooqLinkRepository.add(tgChatId, url.toString());
        if (link.getLastApiUpdate() == null) {
            String domain = LinkUtils.extractDomainFromUrl(url.toString());
            LinkUpdater updater = linkHolder.getUpdaterByDomain(domain);
            updater.setLastUpdate(link);
        }
        return mapToLinkResponse(link);
    }

    @Transactional
    public LinkResponse remove(long tgChatId, URI url) {
        int count = jooqLinkRepository.remove(tgChatId, url.toString());
        return mapToLinkResponse(new Link().setId(tgChatId).setUrl(url).setUpdateAt(OffsetDateTime.MIN)
            .setLastApiUpdate(OffsetDateTime.MAX));
    }

    public List<LinkResponse> listAll(long tgChatId) {
        return jooqLinkRepository.findAllByChatId(tgChatId).stream().map(this::mapToLinkResponse).toList();
    }

    private LinkResponse mapToLinkResponse(Link link) {
        try {
            return new LinkResponse().setId(link.getId()).setUrl(link.getUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
