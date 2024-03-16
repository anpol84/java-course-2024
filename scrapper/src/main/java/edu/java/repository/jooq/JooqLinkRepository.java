package edu.java.repository.jooq;

import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jooq.tables.records.LinkRecord;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.stereotype.Repository;
import static edu.java.repository.jooq.Tables.CHAT_LINK;
import static edu.java.repository.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dsl;

    @Override
    public Link getOrCreate(Link link) {
        LinkRecord linkRecord = dsl.selectFrom(LINK).where(LINK.URL.eq(link.getUrl().toString())).fetchOne();
        if (linkRecord == null) {
            linkRecord = dsl.insertInto(LINK, LINK.URL, LINK.UPDATE_AT)
                .values(link.getUrl().toString(), OffsetDateTime.now())
                .returning(LINK.ID)
                .fetchOne();
            linkRecord.setUrl(link.getUrl().toString());
            linkRecord.setUpdateAt(OffsetDateTime.now());
        }
        return linkRecord.into(Link.class);
    }

    @Override
    public Link insert(Chat chat, Link link) {
        dsl.insertInto(CHAT_LINK, CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
            .values(chat.getId(), link.getId())
            .execute();
        return link;
    }

    @Override
    public int remove(Long chatId, String url) {
        return dsl.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId)
                .and(CHAT_LINK.LINK_ID.in(dsl.selectDistinct(CHAT_LINK.LINK_ID)
                    .from(CHAT_LINK)
                    .join(LINK).on(CHAT_LINK.LINK_ID.eq(LINK.ID))
                    .where(LINK.URL.eq(url)))))
            .execute();
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return dsl.selectFrom(LINK)
            .where(LINK.ID.in(dsl.select(CHAT_LINK.LINK_ID)
                .from(CHAT_LINK)
                .where(CHAT_LINK.CHAT_ID.eq(chatId))
                .fetch()
            ))
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findAll() {
        return dsl.selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findByOldestUpdates(int count) {
        return dsl.selectFrom(LINK)
            .orderBy(LINK.UPDATE_AT)
            .limit(count)
            .fetch()
            .map(linkRecord -> new Link()
                .setId(linkRecord.getId())
                .setUrl(URI.create(linkRecord.getUrl()))
                .setUpdateAt(linkRecord.getUpdateAt())
                .setLastApiUpdate(linkRecord.getLastApiUpdate()));
    }

    @Override
    public Link findByChatIdAndUrl(Long chatId, String url) {
        return dsl.select().from(LINK)
            .join(CHAT_LINK).on(CHAT_LINK.LINK_ID.eq(LINK.ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId).and(LINK.URL.eq(url)))
            .fetchOptional()
            .map(linkRecord ->
                new Link()
                    .setId(linkRecord.getValue(LINK.ID))
                    .setUrl(URI.create(linkRecord.getValue(LINK.URL)))
                    .setUpdateAt(linkRecord.getValue(LINK.UPDATE_AT))
                    .setLastApiUpdate(linkRecord.getValue(LINK.LAST_API_UPDATE)))
            .orElse(null);
    }

    @Override
    public void setUpdateAt(String url, OffsetDateTime time) {
        dsl.update(LINK)
            .set(LINK.UPDATE_AT, time)
            .where(LINK.URL.eq(url))
            .execute();
    }

    @Override
    public void setLastApiUpdate(String url, OffsetDateTime time) {
        dsl.update(LINK)
            .set(LINK.LAST_API_UPDATE, time)
            .where(LINK.URL.eq(url))
            .execute();
    }

    @Override
    public List<Long> findChatIdsByUrl(String url) {
        return dsl.selectDistinct(CHAT_LINK.CHAT_ID)
            .from(CHAT_LINK)
            .join(LINK).on(CHAT_LINK.LINK_ID.eq(LINK.ID))
            .where(LINK.URL.eq(url))
            .fetch()
            .map(Record1::value1);
    }

    @Override
    public void deleteUnusedLinks() {
        List<Long> usedLinkIds = dsl.select(CHAT_LINK.LINK_ID)
            .from(CHAT_LINK)
            .fetch()
            .map(Record1::value1);

        dsl.deleteFrom(LINK)
            .where(LINK.ID.notIn(usedLinkIds))
            .execute();
    }
}
