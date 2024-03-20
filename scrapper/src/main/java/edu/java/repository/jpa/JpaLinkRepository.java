package edu.java.repository.jpa;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JpaLinkRepository implements LinkRepository {
    private final JpaLinkRepositoryInterface jpaLinkRepositoryInterface;

    @Override
    public Link getOrCreate(Link link) {
        Link addedLink = jpaLinkRepositoryInterface.findByUrl(link.getUrl());
        if (addedLink == null) {
            addedLink = new Link().setUrl(link.getUrl()).setUpdateAt(OffsetDateTime.now());
            addedLink = jpaLinkRepositoryInterface.save(addedLink);
        }
        return addedLink;
    }

    @Override
    public Link insert(Chat chat, Link link) {
        jpaLinkRepositoryInterface.insert(chat.getId(), link.getId());
        return link;
    }

    @Override
    public int remove(Long chatId, String url) {
        return jpaLinkRepositoryInterface.deleteByChatIdAndLinkUrl(chatId, url);
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return jpaLinkRepositoryInterface.findAllByChatsId(chatId);
    }

    @Override
    public List<Link> findAll() {
        return jpaLinkRepositoryInterface.findAll();
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return jpaLinkRepositoryInterface.findTopByOrderByUpdateAtAsc(count);
    }

    @Override
    public Link findByChatIdAndUrl(Long chatId, String url) {
        return jpaLinkRepositoryInterface.findByChatsIdAndUrl(chatId, URI.create(url));
    }

    @Override
    public void setUpdateAt(String url, OffsetDateTime time) {
        Link link = jpaLinkRepositoryInterface.findByUrl(URI.create(url));
        link.setUpdateAt(time);
        jpaLinkRepositoryInterface.save(link);
    }

    @Override
    public void setLastApiUpdate(String url, OffsetDateTime time) {
        Link link = jpaLinkRepositoryInterface.findByUrl(URI.create(url));
        link.setLastApiUpdate(time);
        jpaLinkRepositoryInterface.save(link);
    }

    @Override
    public List<Long> findChatIdsByUrl(String url) {
        return jpaLinkRepositoryInterface.findChatsIdByUrl(url);
    }

    @Override
    public void deleteUnusedLinks() {
        jpaLinkRepositoryInterface.deleteUnusedLinks();
    }
}

