package edu.java.repository.jdbc;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void add(Long id) {
        try {
            jdbcTemplate.update("INSERT INTO chat VALUES (?)", id);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("The chat is already registered",
                "It is not possible to re-register the chat");
        }
    }

    @Override
    @Transactional
    public int remove(Long id) {
        int count =  jdbcTemplate.update("DELETE FROM chat WHERE id = ?", id);
        if (count == 0) {
            throw new NotFoundException("There is no such chat", "Deleting a non-existent chat is not possible");
        }
        return count;
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
