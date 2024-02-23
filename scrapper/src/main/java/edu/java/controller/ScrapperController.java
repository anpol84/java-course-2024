package edu.java.controller;

import edu.java.service.ScrapperService;
import edu.java.serviceDto.AddLinkRequest;
import edu.java.serviceDto.LinkResponse;
import edu.java.serviceDto.ListLinksResponse;
import edu.java.serviceDto.RemoveLinkRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
    private final ScrapperService scrapperService;

    @PostMapping("/tg-chat/{id}")
    public String registerChat(@PathVariable("id") @Min(1) Long id) {
        scrapperService.registerChat(id);
        return "Чат зарегистрирован";
    }

    @DeleteMapping("/tg-chat/{id}")
    public String deleteChat(@PathVariable("id") @Min(1) Long id) {
        scrapperService.deleteChat(id);
        return "Чат успешно удалён";
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId) {
        List<LinkResponse> links = scrapperService.getLinks(chatId);
        return new ListLinksResponse(links, links.size());
    }

    @PostMapping("/links")
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid AddLinkRequest request) {
        return scrapperService.addLink(chatId, request.getLink());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid RemoveLinkRequest request) {
        return scrapperService.removeLink(chatId, request.getLink());
    }
}
