package edu.java.controller;

import edu.java.service.ChatService;
import edu.java.service.LinkService;
import edu.java.serviceDto.AddLinkRequest;
import edu.java.serviceDto.LinkResponse;
import edu.java.serviceDto.ListLinksResponse;
import edu.java.serviceDto.RemoveLinkRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
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
    private final ChatService chatService;
    private final LinkService linkService;
    Counter messagesProcessed = Metrics.counter("messages.processed");

    @PostMapping("/tg-chat/{id}")
    public String registerChat(@PathVariable("id") @Min(1) Long id) {
        chatService.register(id);
        messagesProcessed.increment();
        return "The chat is registered";
    }

    @DeleteMapping("/tg-chat/{id}")
    public String deleteChat(@PathVariable("id") @Min(1) Long id) {
        chatService.unregister(id);
        messagesProcessed.increment();
        return "The chat was successfully deleted";
    }

    @GetMapping("/links")
    public ListLinksResponse getLinks(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId) {
        List<LinkResponse> links = linkService.listAll(chatId);
        messagesProcessed.increment();
        return new ListLinksResponse()
            .setLinks(links)
            .setSize(links.size());
    }

    @PostMapping("/links")
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid AddLinkRequest request) {
        messagesProcessed.increment();
        return linkService.add(chatId, request.getLink());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") @Min(1) Long chatId,
        @RequestBody @Valid RemoveLinkRequest request) {
        messagesProcessed.increment();
        return linkService.remove(chatId, request.getLink());
    }



}
