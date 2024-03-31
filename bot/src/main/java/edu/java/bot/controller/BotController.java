package edu.java.bot.controller;

import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class BotController {

    private final ProcessMessageService processMessageService;
    Counter messagesProcessed = Metrics.counter("messages.processed");

    @PostMapping
    public String sendUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdate) {
        processMessageService.process(linkUpdate);
        messagesProcessed.increment();
        return "The update has been processed";
    }
}
