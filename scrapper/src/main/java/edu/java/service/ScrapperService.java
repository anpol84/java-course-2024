package edu.java.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.serviceDto.LinkResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScrapperService {

    private final Map<Long, List<LinkResponse>> chatLinksMap = new HashMap<>();
    private final static String CHAT_NOT_EXIST = "Такого чата не существует";

    public void registerChat(Long chatId) {
        if (chatLinksMap.containsKey(chatId)) {
            throw new BadRequestException("Чат уже зарегистрирован", "Повторная регистрация чата невозможна");
        }
        chatLinksMap.put(chatId, new ArrayList<>());
    }

    public void deleteChat(Long chatId) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new NotFoundException(CHAT_NOT_EXIST, "Удаление несуществующего чата невозможно");
        }
        chatLinksMap.remove(chatId);
    }

    public List<LinkResponse> getLinks(Long chatId) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new NotFoundException(CHAT_NOT_EXIST, "Обращение к несуществующему чату невозможно");
        }
        return chatLinksMap.get(chatId);
    }

    public LinkResponse addLink(Long chatId, URI link) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new NotFoundException(CHAT_NOT_EXIST, "Добавление ссылки в несуществующий чат невозможно");
        }
        List<LinkResponse> links = chatLinksMap.get(chatId);
        for (LinkResponse item : links) {
            if (item.getUrl().getPath().equals(link.getPath())) {
                throw new BadRequestException("Ссылка уже существует", "Повторное добавление ссылки невозможно");
            }
        }
        LinkResponse linkResponse = new LinkResponse((long) (links.size() + 1), link);
        links.add(linkResponse);
        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, URI link) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new NotFoundException(CHAT_NOT_EXIST, "Удаление ссылки из несуществующего чата невозможно");
        }
        List<LinkResponse> links = chatLinksMap.get(chatId);
        for (LinkResponse response : links) {
            if (response.getUrl().getPath().equals(link.getPath())) {
                links.remove(response);
                return response;
            }
        }
        throw new NotFoundException("Ссылки не существует", "Удаление несуществующей ссылки невозможно");
    }
}
