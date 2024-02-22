package edu.java.bot.controller;

import edu.java.bot.service.BotService;
import edu.java.common.requestDto.LinkUpdateRequest;
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

    private final BotService botService;

    @PostMapping
    public String sendUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdate) {
        botService.addUpdate(linkUpdate);
        return "Обновление обработано";
    }
}
