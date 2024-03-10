package edu.java.repository.jooq;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import edu.java.repository.jooq.tables.records.ChatRecord;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Long id) {
        try {
            dslContext.insertInto(Tables.CHAT)
                .set(Tables.CHAT.ID, id)
                .execute();
        } catch (IntegrityConstraintViolationException ex) {
            throw new BadRequestException("The chat is already registered",
                "It is not possible to re-register the chat");
        }
    }

    @Override
    public int remove(Long id) {
        int count =  dslContext.deleteFrom(Tables.CHAT)
                .where(Tables.CHAT.ID.eq(id))
                .execute();
        if (count == 0) {
            throw new NotFoundException("There is no such chat", "Deleting a non-existent chat is not possible");
        }
        return count;
    }

    @Override
    public Optional<Chat> findById(Long id) {
        @Nullable ChatRecord chatRecord = dslContext.selectFrom(Tables.CHAT)
            .where(Tables.CHAT.ID.eq(id))
            .fetchOne();

        return Optional.ofNullable(chatRecord).map(r -> r.into(Chat.class));
    }

    @Override
    public List<Chat> findAll() {
        return dslContext.selectFrom(Tables.CHAT)
            .fetch()
            .map(r -> r.into(Chat.class));
    }
}

