package edu.java.bot.controller;

import edu.java.bot.dto.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> sendUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdate) {
        botService.addUpdate(linkUpdate);
        return ResponseEntity.ok("Обновление обработано");
    }
}
