package edu.java.repository.jpa;

import edu.java.model.Link;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;



public interface JpaLinkRepositoryInterface extends JpaRepository<Link, Long> {
    @Query(value = "SELECT * FROM link WHERE url = :url", nativeQuery = true)
    Link findByUrl(String url);

    @Modifying
    @Query(value = "INSERT INTO chat_link (chat_id, link_id) VALUES (:chatId, :linkId)", nativeQuery = true)
    void insert(Long chatId, Long linkId);

    @Modifying
    @Query(value = """
                DELETE FROM chat_link WHERE chat_id = :chatId AND link_id IN
                (SELECT DISTINCT c.link_id FROM chat_link c JOIN link l ON c.link_id = l.id WHERE url = :url)
                """, nativeQuery = true)
    int deleteByChatIdAndLinkUrl(Long chatId, String url);

    @Modifying
    @Query(value = """
            SELECT * FROM link WHERE id IN
            (SELECT link_id FROM chat_link WHERE chat_id = :chatId)
            """, nativeQuery = true)
    List<Link> findAllByChatId(Long chatId);

    @Query(value = "SELECT * FROM link ORDER BY update_at LIMIT :count", nativeQuery = true)
    List<Link> findTopByOrderByUpdateAtAsc(int count);


    @Query(value = """
            SELECT DISTINCT c.chat_id FROM chat_link c JOIN link l
            ON c.link_id = l.id WHERE l.url = :url
            """, nativeQuery = true)
    List<Long> findChatIdsByUrl(String url);

    @Modifying
    @Query(value = "DELETE FROM link WHERE id NOT IN (SELECT link_id FROM chat_link)", nativeQuery = true)
    void deleteUnusedLinks();

    @Query(value = """
            SELECT DISTINCT * FROM chat_link c JOIN link l
            ON c.link_id = l.id WHERE c.chat_id = :chatId
             AND l.url = :url
             """, nativeQuery = true)
    Link findAllByChatIdAndUrl(Long chatId, String url);

}


