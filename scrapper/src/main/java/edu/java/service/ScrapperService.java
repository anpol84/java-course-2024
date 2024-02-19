package edu.java.service;

import edu.java.dto.LinkResponse;
import edu.java.exception.ChatAlreadyExistException;
import edu.java.exception.ChatNotExistException;
import edu.java.exception.LinkAlreadyExistException;
import edu.java.exception.LinkNotExistException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;


@Service
public class ScrapperService {
    private final Map<Long, List<LinkResponse>> chatLinksMap = new HashMap<>();
    private final static String CHAT_NOT_EXIST = "Такого чата не существует";

    public void registerChat(Long chatId) {
        if (chatLinksMap.containsKey(chatId)) {
            throw new ChatAlreadyExistException("Чат уже зарегистрирован");
        }
        chatLinksMap.put(chatId, new ArrayList<>());
    }

    public void deleteChat(Long chatId) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new ChatNotExistException(CHAT_NOT_EXIST);
        }
        chatLinksMap.remove(chatId);
    }

    public List<LinkResponse> getLinks(Long chatId) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new ChatNotExistException(CHAT_NOT_EXIST);
        }
        return chatLinksMap.get(chatId);
    }

    public LinkResponse addLink(Long chatId, URI link) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new ChatNotExistException(CHAT_NOT_EXIST);
        }
        List<LinkResponse> links = chatLinksMap.get(chatId);
        for (LinkResponse item : links) {
            if (item.getUrl().getPath().equals(link.getPath())) {
                throw new LinkAlreadyExistException("Ссылка уже существует");
            }
        }
        LinkResponse linkResponse = new LinkResponse((long) (links.size() + 1), link);
        links.add(linkResponse);
        return linkResponse;
    }

    public LinkResponse removeLink(Long chatId, URI link) {
        if (!chatLinksMap.containsKey(chatId)) {
            throw new ChatNotExistException(CHAT_NOT_EXIST);
        }
        List<LinkResponse> links = chatLinksMap.get(chatId);
        for (LinkResponse response : links) {
            if (response.getUrl().getPath().equals(link.getPath())) {
                links.remove(response);
                return response;
            }
        }
        throw new LinkNotExistException("Ссылки не существует");
    }
}
