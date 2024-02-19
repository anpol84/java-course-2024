package edu.java.scrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import edu.java.dto.LinkResponse;
import edu.java.exception.ChatAlreadyExistException;
import edu.java.exception.ChatNotExistException;
import edu.java.exception.LinkAlreadyExistException;
import edu.java.exception.LinkNotExistException;
import edu.java.service.ScrapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class ScrapperServiceTest {
    private ScrapperService scrapperService;

    @BeforeEach
    public void init(){
        scrapperService = new ScrapperService();
    }

    @Test
    public void testRegisterChat() {
        scrapperService.registerChat(1L);
        assertThrows(ChatAlreadyExistException.class, () -> scrapperService.registerChat(1L));
    }

    @Test
    public void testDeleteChat() {
        assertThrows(ChatNotExistException.class, () -> scrapperService.deleteChat(1L));
        scrapperService.registerChat(1L);
        scrapperService.deleteChat(1L);
        assertThrows(ChatNotExistException.class, () -> scrapperService.deleteChat(1L));
    }

    @Test
    public void testGetLinks() {
        assertThrows(ChatNotExistException.class, () -> scrapperService.getLinks(1L));
        scrapperService.registerChat(1L);
        List<LinkResponse> links = scrapperService.getLinks(1L);
        assertNotNull(links);
        assertTrue(links.isEmpty());
    }

    @Test
    public void testAddLink() throws URISyntaxException {
        scrapperService.registerChat(1L);
        LinkResponse addedLink1 = scrapperService.addLink(1L, new URI("123"));
        assertNotNull(addedLink1);
        assertThrows(LinkAlreadyExistException.class, () -> scrapperService.addLink(1L, new URI("123")));
        LinkResponse addedLink2 = scrapperService.addLink(1L, new URI("1234"));
        assertNotNull(addedLink2);
    }

    @Test
    public void testRemoveLink() throws URISyntaxException {
        scrapperService.registerChat(1L);
        assertThrows(LinkNotExistException.class, () -> scrapperService.removeLink(1L,  new URI("123")));
        LinkResponse addedLink1 = scrapperService.addLink(1L,  new URI("123"));
        assertNotNull(addedLink1);
        LinkResponse removedLink = scrapperService.removeLink(1L,  new URI("123"));
        assertNotNull(removedLink);
        assertEquals( new URI("123"), removedLink.getUrl());
        assertThrows(LinkNotExistException.class, () -> scrapperService.removeLink(1L,  new URI("1234")));
    }
}
