package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.service.updater.LinkHolder;
import edu.java.service.updater.LinkUpdater;
import edu.java.utils.LinkUtils;
import java.net.URI;
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

    public Link add(long tgChatId, URI url) {
        if (!url.toString().matches(STACKOVERFLOW_REGEX) && !url.toString().matches(GITHUB_REGEX)) {
            throw new BadRequestException("Плохая ссылка", "Данная ссылка не поддерживается");
        }
        try {
            String domain = LinkUtils.extractDomainFromUrl(url.toString());
            LinkUpdater updater = linkHolder.getUpdaterByDomain(domain);
            Link link = linkRepository.add(tgChatId, url.toString());
            updater.process(link);
            return link;
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

    public Link remove(long tgChatId, URI url) {
        try {
            return linkRepository.remove(tgChatId, url.toString());
        } catch (RuntimeException e) {
            throw new NotFoundException("Ресурса не существует", "Чат или ссылка были не найдены");
        }
    }

    public List<Link> listAll(long tgChatId) {
        return linkRepository.findAllByChatId(tgChatId);
    }
}
