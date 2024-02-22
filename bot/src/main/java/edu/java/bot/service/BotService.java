package edu.java.bot.service;

import edu.java.bot.dto.LinkUpdateRequest;
import edu.java.bot.exception.UpdateAlreadyExistException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class BotService {
    private final List<LinkUpdateRequest> updates = new ArrayList<>();

    public void addUpdate(LinkUpdateRequest linkUpdate) {
        for (LinkUpdateRequest request : updates) {
            if (request.getId().equals(linkUpdate.getId())) {
                throw new UpdateAlreadyExistException("Update уже существует");
            }
        }
        updates.add(linkUpdate);
    }
}
