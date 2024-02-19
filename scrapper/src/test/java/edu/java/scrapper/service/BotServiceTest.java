package edu.java.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import edu.java.dto.LinkUpdateRequest;
import edu.java.exception.LinkAlreadyExistException;
import edu.java.service.BotService;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class BotServiceTest {
    @Test
    public void failAddUpdateTest() throws URISyntaxException {
        BotService botService = new BotService();
        LinkUpdateRequest update1 = new LinkUpdateRequest(1L, new URI("123"),
            "Description 1", List.of(1L, 2L));
        botService.addUpdate(update1);
        assertThrows(LinkAlreadyExistException.class, () -> botService.addUpdate(update1));
    }
}
