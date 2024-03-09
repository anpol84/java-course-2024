package edu.java.repository;

import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;


public interface LinkRepository {

    Link add(Long chatId, String url);

    int remove(Long chatId, String url);

    List<Link> findAllByChatId(Long chatId);

    List<Link> findAll();

    List<Link> findByOldestUpdates(int count);

    Link findByChatIdAndUrl(Long chatId, String url);

    void setUpdateAt(String url, OffsetDateTime time);

    void setLastApiUpdate(String url, OffsetDateTime time);

    List<Long> findChatIdsByUrl(String url);

    void deleteUnusedLinks();
}
