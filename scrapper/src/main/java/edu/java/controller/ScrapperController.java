package edu.java.controller;

import edu.java.model.Link;
import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.serviceDto.AddLinkRequest;
import edu.java.serviceDto.LinkResponse;
import edu.java.serviceDto.ListLinksResponse;
import edu.java.serviceDto.RemoveLinkRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ScrapperController {
    private final ChatService jdbcChatService;
    private final LinkService jdbcLinkService;

    @PostMapping("/tg-chat/{id}")
    public String registerChat(@PathVariable("id") @Min(1) Long id) {
        jdbcChatService.register(id);
        return "Чат зарегистрирован";
    }

    @DeleteMapping("/tg-chat/{id}")
    public String deleteChat(@PathVariable("id") @Min(1) Long id) {
        jdbcChatService.unregister(id);
        return "Чат успешно удалён";
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId) {
        List<LinkResponse> links = jdbcLinkService.listAll(chatId).stream().map(this::mapToLinkResponse).toList();
        return new ListLinksResponse(links, links.size());
    }

    @PostMapping("/links")
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid AddLinkRequest request) {
        return mapToLinkResponse(jdbcLinkService.add(chatId, request.getLink()));
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid RemoveLinkRequest request) {
        return mapToLinkResponse(jdbcLinkService.remove(chatId, request.getLink()));
    }

    private LinkResponse mapToLinkResponse(Link link) {
        try {
            return new LinkResponse(link.getId(), new URI(link.getUrl()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
