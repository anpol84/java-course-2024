package edu.java.controller;

import edu.java.dto.LinkUpdateRequest;
import edu.java.service.BotService;
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
