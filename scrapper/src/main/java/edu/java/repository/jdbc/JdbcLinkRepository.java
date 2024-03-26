package edu.java.repository.jdbc;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


@RequiredArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcTemplate jdbcTemplate;
    private final static String LINK_ID = "link_id";
    private final static String URL = "url";
    private final static String UPDATE_AT = "update_at";
    private final static String LAST_API_UPDATE = "last_api_update";

    @Override
    public Link getOrCreate(Link link) {
        Long linkId;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Link addedLink = null;
        try {
            addedLink = jdbcTemplate.queryForObject("SELECT * FROM link WHERE url = ?", (rs, rowNum) ->
                new Link().setId(rs.getLong("id"))
                    .setUrl(URI.create(rs.getString(URL)))
                    .setUpdateAt(rs.getObject(UPDATE_AT, OffsetDateTime.class))
                    .setLastApiUpdate(rs.getObject(LAST_API_UPDATE, OffsetDateTime.class)), link.getUrl().toString());
        } catch (EmptyResultDataAccessException ignored) {
        }
        if (addedLink == null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    """
                        INSERT INTO link
                        (url, update_at) VALUES (?, CURRENT_TIMESTAMP)
                        """,
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, link.getUrl().toString());
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            linkId = (Long) keys.get("id");
            addedLink = new Link().setId(linkId)
                .setUrl(link.getUrl())
                .setUpdateAt(OffsetDateTime.now());
        }
        return addedLink;
    }

    @Override
    public Link insert(Chat chat, Link link) {
        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chat.getId(), link.getId());
        return link;
    }


    @Override
    public int remove(Long chatId, String url) {
        return jdbcTemplate.update("""
                DELETE FROM chat_link WHERE chat_id = ? AND link_id IN
                (SELECT DISTINCT c.link_id FROM chat_link c JOIN link l ON c.link_id = l.id WHERE url = ?)
                """, chatId,
            url
        );
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return jdbcTemplate.query("""
            SELECT * FROM link WHERE id IN
            (SELECT link_id FROM chat_link WHERE chat_id = ?)
            """, (rs, rowNum) ->
            new Link().setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString(URL)))
                .setUpdateAt(rs.getObject(UPDATE_AT, OffsetDateTime.class))
                .setLastApiUpdate(rs.getObject(LAST_API_UPDATE, OffsetDateTime.class)), chatId);
    }

    @Override
    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", (rs, rowNum) ->
            new Link().setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString(URL)))
                .setUpdateAt(rs.getObject(UPDATE_AT, OffsetDateTime.class))
                .setLastApiUpdate(rs.getObject(LAST_API_UPDATE, OffsetDateTime.class)));
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return jdbcTemplate.query("SELECT * FROM link ORDER BY update_at LIMIT ?", (rs, rowNum) ->
            new Link().setId(rs.getLong("id"))
                .setUrl(URI.create(rs.getString(URL)))
                .setUpdateAt(rs.getObject(UPDATE_AT, OffsetDateTime.class))
                .setLastApiUpdate(rs.getObject(LAST_API_UPDATE, OffsetDateTime.class)), count);
    }

    @Override
    public Link findByChatIdAndUrl(Long chatId, String url) {
        List<Link> links = jdbcTemplate.query("""
            SELECT DISTINCT * FROM chat_link c JOIN link l
            ON c.link_id = l.id WHERE c.chat_id = ?
             AND l.url = ?
             """, (rs, rowNum) -> new Link().setId(rs.getLong(LINK_ID))
            .setUrl(URI.create(rs.getString(URL)))
            .setUpdateAt(rs.getObject(UPDATE_AT, OffsetDateTime.class))
            .setLastApiUpdate(rs.getObject(LAST_API_UPDATE, OffsetDateTime.class)), chatId, url);
        return links.isEmpty() ? null : links.get(0);
    }

    @Override
    public void setUpdateAt(String url, OffsetDateTime time) {
        jdbcTemplate.update("UPDATE link SET update_at = ? WHERE url = ?", time, url);
    }

    @Override
    public void setLastApiUpdate(String url, OffsetDateTime time) {
        jdbcTemplate.update("UPDATE link SET last_api_update= ? WHERE url = ?", time, url);
    }

    @Override
    public List<Long> findChatIdsByUrl(String url) {
        return jdbcTemplate.query("""
            SELECT DISTINCT c.chat_id FROM chat_link c JOIN link l
            ON c.link_id = l.id WHERE l.url = ?
            """, (rs, rowNum) -> rs.getLong("chat_id"), url);
    }

    @Override
    public void deleteUnusedLinks() {
        jdbcTemplate.update("DELETE FROM link WHERE id NOT IN (SELECT link_id FROM chat_link)");
    }
}
