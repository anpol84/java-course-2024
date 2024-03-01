package edu.java.scrapper.service;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.JdbcChatRepository;
import edu.java.repository.JdbcLinkRepository;
import edu.java.service.JdbcLinkService;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class JdbcLinkServiceTest {
    @Test
    public void correctListAllTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        when(jdbcLinkRepository.findAllByChatId(1L))
            .thenReturn(List.of(new Link(1L, "some", OffsetDateTime.MAX)));
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        List<Link> links = linkService.listAll(1L);
        assertEquals(1, links.size());
        assertEquals(1, links.get(0).getId());
        verify(jdbcChatRepository).findById(1L);
        verify(jdbcLinkRepository).findAllByChatId(1L);
    }

    @Test
    public void badListAllTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(null);
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            List<Link> links = linkService.listAll(1L);
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Такого чата не существует");
            assertEquals(e.getDescription(), "Просмотр ссылок несуществующего чата невозможно");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository, never()).findAllByChatId(anyLong());
        }
    }

    @Test
    public void noChatAddTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(null);
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.add(1L, new URI("http://example.ru"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Такого чата не существует");
            assertEquals(e.getDescription(),  "Добавление ссылки в несуществующий чат невозможно");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository, never()).findByChatIdAndUrl(anyLong(), anyString());
            verify(jdbcLinkRepository, never()).add(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void badLinkAddTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.add(1L, new URI("http://example.ru"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "Плохая ссылка");
            assertEquals(e.getDescription(),  "Данная ссылка не поддерживается");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository, never()).findByChatIdAndUrl(anyLong(), anyString());
            verify(jdbcLinkRepository, never()).add(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void linkAlreadyExistAddTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        when(jdbcLinkRepository.findByChatIdAndUrl(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link());
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
        }catch (BadRequestException e){
            assertEquals(e.getMessage(), "Ссылка уже существует");
            assertEquals(e.getDescription(),  "Повторное добавление ссылки невозможно");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository).findByChatIdAndUrl(1L, "https://github.com/anpol84/test");
            verify(jdbcLinkRepository, never()).add(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctAddTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        when(jdbcLinkRepository.findByChatIdAndUrl(1L, "https://github.com/anpol84/test"))
            .thenReturn(null);
        when(jdbcLinkRepository.add(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link(1L, "https://github.com/anpol84/test", OffsetDateTime.MAX));
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.add(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl());
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository).findByChatIdAndUrl(1L, "https://github.com/anpol84/test");
            verify(jdbcLinkRepository).add(1L, "https://github.com/anpol84/test");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void noChatRemoveTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(null);
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.remove(1L, new URI("http://example.ru"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Такого чата не существует");
            assertEquals(e.getDescription(),  "Удаление ссылки из несуществующего чата невозможно");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository, never()).findByChatIdAndUrl(anyLong(), anyString());
            verify(jdbcLinkRepository, never()).remove(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void linkNotExistRemoveTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        when(jdbcLinkRepository.findByChatIdAndUrl(1L, "https://github.com/anpol84/test"))
            .thenReturn(null);
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.remove(1L, new URI("https://github.com/anpol84/test"));
        }catch (NotFoundException e){
            assertEquals(e.getMessage(), "Ссылки не существует");
            assertEquals(e.getDescription(),  "Удаление несуществующей ссылки невозможно");
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository).findByChatIdAndUrl(1L, "https://github.com/anpol84/test");
            verify(jdbcLinkRepository, never()).remove(anyLong(), anyString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void correctRemoveTest(){
        JdbcLinkRepository jdbcLinkRepository = mock(JdbcLinkRepository.class);
        JdbcChatRepository jdbcChatRepository = mock(JdbcChatRepository.class);
        when(jdbcChatRepository.findById(1L)).thenReturn(new Chat(1L));
        when(jdbcLinkRepository.findByChatIdAndUrl(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link(1L, "https://github.com/anpol84/test", OffsetDateTime.MAX));
        when(jdbcLinkRepository.remove(1L, "https://github.com/anpol84/test"))
            .thenReturn(new Link(1L, "https://github.com/anpol84/test", OffsetDateTime.MAX));
        JdbcLinkService linkService = new JdbcLinkService(jdbcLinkRepository, jdbcChatRepository);
        try {
            Link link = linkService.remove(1L, new URI("https://github.com/anpol84/test"));
            assertEquals(1, link.getId());
            assertEquals("https://github.com/anpol84/test", link.getUrl());
            verify(jdbcChatRepository).findById(1L);
            verify(jdbcLinkRepository).findByChatIdAndUrl(1L, "https://github.com/anpol84/test");
            verify(jdbcLinkRepository).remove(1L, "https://github.com/anpol84/test");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
