package edu.java.bot.service;

import edu.java.bot.exception.BadRequestException;
import edu.java.bot.serviceDto.LinkUpdateRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class BotService {
    private final List<LinkUpdateRequest> updates = new ArrayList<>();

    public void addUpdate(LinkUpdateRequest linkUpdate) {
        for (LinkUpdateRequest request : updates) {
            if (request.getId().equals(linkUpdate.getId())) {
                throw new BadRequestException("Update уже существует", "Нельзя добавить 2 раза один и тот же update");
            }
        }
        updates.add(linkUpdate);
    }
}
