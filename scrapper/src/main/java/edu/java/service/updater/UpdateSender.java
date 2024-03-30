package edu.java.service.updater;

import edu.java.clientDto.LinkUpdateRequest;

public interface UpdateSender {
    void send(LinkUpdateRequest request);
}
