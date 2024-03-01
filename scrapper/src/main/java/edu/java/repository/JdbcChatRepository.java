package edu.java.repository;

import edu.java.model.Chat;
import edu.java.model.Link;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@RequiredArgsConstructor
public class JdbcChatRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JdbcLinkRepository linkRepository;

    @Transactional
    public void add(Long id) {
        jdbcTemplate.update("INSERT INTO chat VALUES (?)", id);
    }

    @Transactional
    public void remove(Long id) {
        List<Link> links = jdbcTemplate.query("SELECT DISTINCT * FROM chat_link c JOIN link l"
                + " ON c.link_id = l.id WHERE c.chat_id = ?",
            (rs, rowNum) -> new Link(rs.getLong("link_id"), rs.getString("url"),
                rs.getObject("last_update", OffsetDateTime.class)), id);
        for (Link link : links) {
            linkRepository.remove(id, link.getUrl());
        }
        jdbcTemplate.update("DELETE FROM chat WHERE id = ?", id);
    }

    public Chat findById(Long id) {
        List<Chat> chats = jdbcTemplate.query("SELECT * FROM chat WHERE id = ?",
            (rs, rowNum) -> new Chat(rs.getLong("id")), id);
        return chats.isEmpty() ? null : chats.get(0);
    }

    public List<Chat> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat", (rs, rowNum) -> new Chat(rs.getLong("id")));
    }
}
