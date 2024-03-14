package edu.java.repository.jdbc;

import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void add(Chat chat) {
        jdbcTemplate.update("INSERT INTO chat VALUES (?)", chat.getId());
    }

    @Override
    @Transactional
    public int remove(Long id) {
        return jdbcTemplate.update("DELETE FROM chat WHERE id = ?", id);
    }

    @Override
    public Optional<Chat> findById(Long id) {
        var chat = jdbcTemplate.queryForObject("SELECT * FROM chat WHERE id = ?",
            (rs, rowNum) -> new Chat().setId(rs.getLong("id")), id);
        return Optional.ofNullable(chat);
    }

    @Override
    public List<Chat> findAll() {
        return jdbcTemplate.query("SELECT * FROM chat", (rs, rowNum) ->
            new Chat().setId(rs.getLong("id")));
    }
}
