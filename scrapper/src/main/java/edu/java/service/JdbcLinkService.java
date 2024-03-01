package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Link;
import edu.java.repository.JdbcChatRepository;
import edu.java.repository.JdbcLinkRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatRepository jdbcChatRepository;
    private final static String CHAT_NOT_EXIST = "Такого чата не существует";

    private final static String STACKOVERFLOW_REGEX = "https://stackoverflow\\.com/questions/\\d+";
    private final static String GITHUB_REGEX = "https://github\\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+";

    @Override
    public Link add(long tgChatId, URI url) {
        if (jdbcChatRepository.findById(tgChatId) == null) {
            throw new NotFoundException(CHAT_NOT_EXIST,
                "Добавление ссылки в несуществующий чат невозможно");
        }
        if (!url.toString().matches(STACKOVERFLOW_REGEX) && !url.toString().matches(GITHUB_REGEX)) {
            throw new BadRequestException("Плохая ссылка", "Данная ссылка не поддерживается");
        }
        if (jdbcLinkRepository.findByChatIdAndUrl(tgChatId, url.toString()) != null) {
            throw new BadRequestException("Ссылка уже существует", "Повторное добавление ссылки невозможно");
        }
        return jdbcLinkRepository.add(tgChatId, url.toString());
    }

    @Override
    public Link remove(long tgChatId, URI url) {
        if (jdbcChatRepository.findById(tgChatId) == null) {
            throw new NotFoundException(CHAT_NOT_EXIST,
                "Удаление ссылки из несуществующего чата невозможно");
        }
        if (jdbcLinkRepository.findByChatIdAndUrl(tgChatId, url.toString()) == null) {
            throw new NotFoundException("Ссылки не существует", "Удаление несуществующей ссылки невозможно");
        }
        return jdbcLinkRepository.remove(tgChatId, url.toString());
    }

    @Override
    public List<Link> listAll(long tgChatId) {
        if (jdbcChatRepository.findById(tgChatId) == null) {
            throw new NotFoundException(CHAT_NOT_EXIST, "Просмотр ссылок несуществующего чата невозможно");
        }
        return jdbcLinkRepository.findAllByChatId(tgChatId);
    }
}
