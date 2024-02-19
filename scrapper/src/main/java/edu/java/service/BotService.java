package edu.java.service;

import edu.java.dto.LinkUpdateRequest;
import edu.java.exception.LinkAlreadyExistException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class BotService {
    private final List<LinkUpdateRequest> updates = new ArrayList<>();

    public void addUpdate(LinkUpdateRequest linkUpdate) {
        for (LinkUpdateRequest request : updates) {
            if (request.getId().equals(linkUpdate.getId())) {
                throw new LinkAlreadyExistException("Update уже существует");
            }
        }
        updates.add(linkUpdate);
    }
}
