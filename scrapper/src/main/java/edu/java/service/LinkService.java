package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
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
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;
    private final static String CHAT_NOT_EXIST = "There is no such chat";

    private final static String NO_CHAT_MESSAGE_JDBC =
        "ERROR: insert or update on table \"chat_link\" violates foreign key constraint \"fk_chat_id\"";
    private final static String DUPLICATE_MESSAGE =
        "ERROR: duplicate key value violates unique constraint \"chat_link_pkey\"";
    private final static String NO_CHAT_MESSAGE_JOOQ =
        "is not present in table \"chat\"";

    @Transactional(rollbackFor = Exception.class)
    public LinkResponse add(long tgChatId, URI url) {
        if (!LinkUtils.validateLink(url.toString())) {
            throw new BadRequestException("Bad link", "This link is not supported");
        }
        try {
            Link link = linkRepository.getOrCreate(new Link().setUrl(url));
            link = linkRepository.insert(new Chat().setId(tgChatId), link);
            if (link.getLastApiUpdate() == null) {
                String domain = LinkUtils.extractDomainFromUrl(url.toString());
                LinkUpdater updater = linkHolder.getUpdaterByDomain(domain);
                updater.setLastUpdate(link);
            }
            return mapToLinkResponse(link);
        } catch (DataIntegrityViolationException | IntegrityConstraintViolationException ex) {
            if (ex.getMessage().contains(NO_CHAT_MESSAGE_JDBC) || ex.getMessage().contains(NO_CHAT_MESSAGE_JOOQ)) {
                throw new NotFoundException(CHAT_NOT_EXIST,
                    "The bot is not available until the /start command. Enter it to start working with the bot");
            } else if (ex.getMessage().contains(DUPLICATE_MESSAGE)
                || ex.getMessage().contains(DUPLICATE_MESSAGE)) {
                throw new BadRequestException("The link already exists", "It is not possible to add the link again");
            } else {
                throw new RuntimeException();
            }
        }
    }

    @Transactional
    public LinkResponse remove(long tgChatId, URI url) {
        int count = linkRepository.remove(tgChatId, url.toString());
        if (count == 0) {
            throw new NotFoundException("The resource does not exist", "The chat or link was not found");
        }
        return mapToLinkResponse(new Link()
            .setId(tgChatId)
            .setUrl(url)
            .setUpdateAt(OffsetDateTime.MIN)
            .setLastApiUpdate(OffsetDateTime.MAX));
    }

    public List<LinkResponse> listAll(long tgChatId) {
        return linkRepository.findAllByChatId(tgChatId).stream().map(this::mapToLinkResponse).toList();
    }

    private LinkResponse mapToLinkResponse(Link link) {
        try {
            return new LinkResponse()
                .setId(link.getId())
                .setUrl(link.getUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
