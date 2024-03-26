package edu.java.bot.controller;

import edu.java.bot.service.ProcessMessageService;
import edu.java.bot.serviceDto.LinkUpdateRequest;
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

    @PostMapping
    public String sendUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdate) {
        processMessageService.process(linkUpdate);
        return "The update has been processed";
    }
}
