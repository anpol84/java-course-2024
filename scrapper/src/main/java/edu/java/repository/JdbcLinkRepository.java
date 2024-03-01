package edu.java.repository;

import edu.java.model.Link;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository {
    private final JdbcTemplate jdbcTemplate;
    private final static String LINK_ID = "link_id";
    private final static String URL = "url";
    private final static String LAST_UPDATE = "last_update";

    @Transactional
    public Link add(Long chatId, String url) {
        Integer linkId;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        List<Integer> ids = jdbcTemplate.query("SELECT id from link where url = ?",
            (rs, rowNum) -> rs.getInt("id"), url);
        if (ids.size() == 0) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO link (url) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, url);
                return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            linkId = (Integer) keys.get("id");
        } else {
            linkId = ids.get(0);
        }
        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", chatId, linkId);
        return new Link(Long.valueOf(linkId), url, OffsetDateTime.now(ZoneOffset.UTC));
    }

    @Transactional
    public Link remove(Long chatId, String url) {
        Long linkId = jdbcTemplate.queryForObject("SELECT id FROM link WHERE url = ?", Long.class, url);
        jdbcTemplate.update("DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?", chatId, linkId);
        List<Integer> ids = jdbcTemplate.query("SELECT link_id from chat_link where link_id = ?",
            (rs, rowNum) -> rs.getInt(LINK_ID), linkId);
        if (ids.size() == 0) {
            jdbcTemplate.update("DELETE FROM link WHERE id = ?", linkId);
        }
        return new Link(linkId, url, OffsetDateTime.now(ZoneOffset.UTC));
    }

    public List<Link> findAllByChatId(Long chatId) {
        return jdbcTemplate.query("SELECT * FROM link WHERE id IN"
            + " (SELECT link_id FROM chat_link WHERE chat_id = ?)", (rs, rowNum) ->
            new Link(rs.getLong("id"),
                rs.getString(URL), rs.getObject(LAST_UPDATE, OffsetDateTime.class)), chatId);
    }

    public List<Link> findAll() {
        return jdbcTemplate.query("SELECT * FROM link", (rs, rowNum) ->
            new Link(rs.getLong("id"),
                rs.getString(URL), rs.getObject(LAST_UPDATE, OffsetDateTime.class)));
    }

    public List<Link> findByLastActivity(int count) {
        return jdbcTemplate.query("SELECT * FROM link ORDER BY last_update LIMIT ?", (rs, rowNum) ->
            new Link(rs.getLong("id"), rs.getString(URL),
                rs.getObject(LAST_UPDATE, OffsetDateTime.class)), count);
    }

    public Link findByChatIdAndUrl(Long chatId, String url) {
        List<Link> links = jdbcTemplate.query("SELECT DISTINCT * FROM chat_link c JOIN link l "
                + "ON c.link_id = l.id WHERE c.chat_id = ?"
                + " AND l.url = ?", (rs, rowNum) -> new Link(rs.getLong(LINK_ID),
            rs.getString(URL), rs.getObject(LAST_UPDATE, OffsetDateTime.class)),
            chatId, url);
        return links.isEmpty() ? null : links.get(0);
    }

    public void updateTime(String url, OffsetDateTime time) {
        jdbcTemplate.update("UPDATE link SET last_update = ? WHERE url = ?", time, url);
    }

    public List<Long> findChatIdsByUrl(String url) {
        return jdbcTemplate.query("SELECT DISTINCT c.chat_id FROM chat_link c JOIN link l "
            + "ON c.link_id = l.id WHERE l.url = ?", (rs, rowNum) -> rs.getLong("chat_id"), url);
    }
}
