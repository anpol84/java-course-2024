package edu.java.controller;

import edu.java.dto.AddLinkRequest;
import edu.java.dto.LinkResponse;
import edu.java.dto.ListLinksResponse;
import edu.java.dto.RemoveLinkRequest;
import edu.java.exception.BadPathParameterException;
import edu.java.service.ScrapperService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> registerChat(@PathVariable("id") Long id) {
        check(id);
        scrapperService.registerChat(id);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<String> deleteChat(@PathVariable("id") Long id) {
        check(id);
        scrapperService.deleteChat(id);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long chatId) {
        check(chatId);
        List<LinkResponse> links = scrapperService.getLinks(chatId);
        return ResponseEntity.ok(new ListLinksResponse(links, links.size()));
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid AddLinkRequest request) {
        check(chatId);
        LinkResponse response = scrapperService.addLink(chatId, request.getLink());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> removeLink(@RequestHeader("Tg-Chat-Id") Long chatId,
        @RequestBody @Valid RemoveLinkRequest request) {
        check(chatId);
        LinkResponse response = scrapperService.removeLink(chatId, request.getLink());
        return ResponseEntity.ok(response);
    }

    private void check(Long chatId) {
        if (chatId <= 0) {
            throw new BadPathParameterException("Id не может быть меньше нуля");
        }
    }
}
