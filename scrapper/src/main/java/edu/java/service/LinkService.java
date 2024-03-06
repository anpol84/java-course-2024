package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final LinkHolder linkHolder;
    private final static String CHAT_NOT_EXIST = "Такого чата не существует";
    private final static String STACKOVERFLOW_REGEX = "https://stackoverflow\\.com/questions/\\d+";
    private final static String GITHUB_REGEX = "https://github\\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+";
    private final static String NO_CHAT_MESSAGE =
        "ERROR: insert or update on table \"chat_link\" violates foreign key constraint \"fk_chat_id\"";
    private final static String DUPLICATE_MESSAGE =
        "ERROR: duplicate key value violates unique constraint \"chat_link_pkey\"";

    public LinkResponse add(long tgChatId, URI url) {
        if (!url.toString().matches(STACKOVERFLOW_REGEX) && !url.toString().matches(GITHUB_REGEX)) {
            throw new BadRequestException("Плохая ссылка", "Данная ссылка не поддерживается");
        }
        try {
            Link link = linkRepository.add(tgChatId, url.toString());
            if (link.getLastApiUpdate().equals(OffsetDateTime.MIN)) {
                String domain = LinkUtils.extractDomainFromUrl(url.toString());
                LinkUpdater updater = linkHolder.getUpdaterByDomain(domain);
                updater.setLastUpdate(link);
            }
            return mapToLinkResponse(link);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains(NO_CHAT_MESSAGE)) {
                throw new NotFoundException(CHAT_NOT_EXIST,
                    "Бот не доступен до команды /start. Введите ее, чтобы начать работу с ботом");
            } else if (ex.getMessage().contains(DUPLICATE_MESSAGE)) {
                throw new BadRequestException("Ссылка уже существует", "Повторное добавление ссылки невозможно");
            } else {
                throw new RuntimeException();
            }
        }
    }

    public LinkResponse remove(long tgChatId, URI url) {

        int count = linkRepository.remove(tgChatId, url.toString());
        if (count == 0) {
            throw new NotFoundException("Ресурса не существует", "Чат или ссылка были не найдены");
        }
        return mapToLinkResponse(new Link(tgChatId, url.toString(), OffsetDateTime.MIN, OffsetDateTime.MAX));
    }

    public List<LinkResponse> listAll(long tgChatId) {
        return linkRepository.findAllByChatId(tgChatId).stream().map(this::mapToLinkResponse).toList();
    }

    private LinkResponse mapToLinkResponse(Link link) {
        try {
            return new LinkResponse(link.getId(), new URI(link.getUrl()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
