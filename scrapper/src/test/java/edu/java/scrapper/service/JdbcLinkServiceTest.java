package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jdbc.JdbcChatRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.service.LinkService;
import edu.java.service.updater.LinkHolder;
import edu.java.service.updater.LinkUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class JdbcLinkServiceTest {

    private LinkHolder linkHolder = mock(LinkHolder.class);

    @Test
    public void correctListAllTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.findAllByChatId(1L))
            .thenReturn(List.of(new Link(1L, "some", OffsetDateTime.MAX, OffsetDateTime.MIN)));
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        List<Link> links = linkService.listAll(1L);
        assertEquals(1, links.size());
        assertEquals(1, links.get(0).getId());
        verify(linkRepository).findAllByChatId(1L);
    }

    @Test
    public void noChatAddTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        when(linkRepository.add(1L, "https://github.com/some/some"))
            .thenThrow(new DataIntegrityViolationException("ERROR: insert or update on table \"chat_link\" " +
                "violates foreign key constraint \"fk_chat_id\""));
        try {
            Link link = linkService.add(1L, new URI("https://github.com/some/some"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Такого чата не существует");
            assertEquals(e.getDescription(),
                "Бот не доступен до команды /start. Введите ее, чтобы начать работу с ботом");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badLinkAddTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            Link link = linkService.add(1L, new URI("http://example.ru"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "Плохая ссылка");
            assertEquals(e.getDescription(),  "Данная ссылка не поддерживается");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void linkAlreadyExistAddTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/some/some"))
            .thenThrow(new DataIntegrityViolationException("ERROR: duplicate key value" +
                " violates unique constraint \"chat_link_pkey\""));
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        LinkUpdater linkUpdater = mock(LinkUpdater.class);
        when(linkHolder.getUpdaterByDomain(anyString())).thenReturn(linkUpdater);
        when(linkUpdater.process(any())).thenReturn(1);
        try {
            Link link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "Ссылка уже существует");
            assertEquals(e.getDescription(),  "Повторное добавление ссылки невозможно");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctAddTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link(1L, "https://github.com/anpol84/test", OffsetDateTime.MAX, OffsetDateTime.MAX));
        LinkHolder linkHolder = mock(LinkHolder.class);
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        LinkUpdater linkUpdater = mock(LinkUpdater.class);
        when(linkHolder.getUpdaterByDomain(anyString())).thenReturn(linkUpdater);
        when(linkUpdater.process(any())).thenReturn(1);
        try {
            Link link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badDataRemoveTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.add(1L, "https://github.com/some/some"))
            .thenThrow(new RuntimeException());
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            Link link = linkService.remove(1L, new URI("https://github.com/some/some"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Ресурса не существует");
            assertEquals(e.getDescription(),  "Чат или ссылка были не найдены");
            verify(linkRepository, never()).findByChatIdAndUrl(anyLong(), anyString());
            verify(linkRepository, never()).remove(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctRemoveTest(){
        LinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.remove(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link(1L, "https://github.com/anpol84/test", OffsetDateTime.MAX, OffsetDateTime.MAX));
        LinkService linkService = new LinkService(linkRepository, linkHolder);
        try {
            Link link = linkService.remove(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl());
            verify(linkRepository).remove(1L, "https://github.com/anpol84/test");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
