package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.service.LinkService;
import edu.java.service.updater.LinkHolder;
import edu.java.service.updater.LinkUpdater;
import edu.java.serviceDto.LinkResponse;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class LinkServiceTest {

    private final LinkHolder linkHolder = mock(LinkHolder.class);

    @Test
    public void correctListAllTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        when(linkRepository.findAllByChatId(1L))
            .thenReturn(List.of(new Link().setId(1L).setUrl(URI.create("some")).setUpdateAt(OffsetDateTime.MAX)
                .setLastApiUpdate(OffsetDateTime.MIN)));
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        List<LinkResponse> links = linkService.listAll(1L);
        assertEquals(1, links.size());
        assertEquals(1, links.get(0).getId());
        verify(linkRepository).findAllByChatId(1L);
    }

    @Test
    public void noChatAddTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        when(linkRepository.add(1L, "https://github.com/some/some"))
            .thenThrow(new NotFoundException("There is no such chat",
                "The bot is not available until the /start command. Enter it to start working with the bot" ));
        try {
            LinkResponse link = linkService.add(1L, new URI("https://github.com/some/some"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "There is no such chat");
            assertEquals(e.getDescription(),
                "The bot is not available until the /start command. Enter it to start working with the bot");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badLinkAddTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            LinkResponse link = linkService.add(1L, new URI("http://example.ru"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "Bad link");
            assertEquals(e.getDescription(),  "This link is not supported");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void linkAlreadyExistAddTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/anpol84/test"))
            .thenThrow(new BadRequestException("The link already exists", "It is not possible to add the link again"));
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        LinkUpdater linkUpdater = mock(LinkUpdater.class);
        when(linkHolder.getUpdaterByDomain(anyString())).thenReturn(linkUpdater);
        when(linkUpdater.process(any())).thenReturn(1);
        try {
            LinkResponse link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "The link already exists");
            assertEquals(e.getDescription(),  "It is not possible to add the link again");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctAddTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link().setId(1L).setUrl(URI.create("https://github.com/anpol84/test"))
                .setUpdateAt(OffsetDateTime.MAX).setLastApiUpdate(OffsetDateTime.MAX));
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        LinkUpdater linkUpdater = mock(LinkUpdater.class);
        when(linkHolder.getUpdaterByDomain(anyString())).thenReturn(linkUpdater);
        when(linkUpdater.process(any())).thenReturn(1);
        try {
            LinkResponse link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badDataRemoveTest(){
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/some/some"))
            .thenThrow(new RuntimeException());
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            LinkResponse link = linkService.remove(1L, new URI("https://github.com/some/some"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "The resource does not exist");
            assertEquals(e.getDescription(),  "The chat or link was not found");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctRemoveTest() {
        LinkRepository linkRepository = mock(JooqLinkRepository.class);
        when(linkRepository.remove(1L, "https://github.com/anpol84/test"))
            .thenReturn(1);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            LinkResponse link = linkService.remove(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl().toString());
            verify(linkRepository).remove(1L, "https://github.com/anpol84/test");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
