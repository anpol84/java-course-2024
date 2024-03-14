package edu.java.repository.jooq;

import edu.java.model.Chat;
import edu.java.repository.ChatRepository;
import edu.java.repository.jooq.tables.records.ChatRecord;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Chat chat) {
        dslContext.insertInto(Tables.CHAT)
                .set(Tables.CHAT.ID, chat.getId())
                .execute();
    }

    @Override
    public int remove(Long id) {
        return dslContext.deleteFrom(Tables.CHAT)
                .where(Tables.CHAT.ID.eq(id))
                .execute();
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

