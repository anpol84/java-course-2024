package edu.java.repository.jooq;

import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.Link;
import edu.java.repository.LinkRepository;
import edu.java.repository.jooq.tables.records.LinkRecord;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.stereotype.Repository;
import static edu.java.repository.jooq.Tables.CHAT_LINK;
import static edu.java.repository.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dsl;

    private final static String CHAT_NOT_EXIST = "There is no such chat";

    private final static String NO_CHAT_MESSAGE =
        "is not present in table \"chat\"";
    private final static String DUPLICATE_MESSAGE =
        "ERROR: duplicate key value violates unique constraint \"chat_link_pkey\"";

    @Override
    public Link add(Long chatId, String url) {
        try {
            LinkRecord linkRecord = dsl.selectFrom(LINK).where(LINK.URL.eq(url)).fetchOne();
            if (linkRecord == null) {
                linkRecord = dsl.insertInto(LINK, LINK.URL, LINK.UPDATE_AT)
                    .values(url, OffsetDateTime.now())
                    .returning(LINK.ID)
                    .fetchOne();
                linkRecord.setUrl(url);
                linkRecord.setUpdateAt(OffsetDateTime.now());
            }
            dsl.insertInto(CHAT_LINK, CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
                .values(chatId, linkRecord.getId())
                .execute();
            return linkRecord.into(Link.class);
        } catch (IntegrityConstraintViolationException ex) {
            if (ex.getMessage().contains(NO_CHAT_MESSAGE)) {
                throw new NotFoundException(CHAT_NOT_EXIST,
                    "The bot is not available until the /start command. Enter it to start working with the bot");
            } else if (ex.getMessage().contains(DUPLICATE_MESSAGE)) {
                throw new BadRequestException("The link already exists", "It is not possible to add the link again");
            } else {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public int remove(Long chatId, String url) {
        int count =  dsl.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId)
                .and(CHAT_LINK.LINK_ID.in(dsl.selectDistinct(CHAT_LINK.LINK_ID)
                    .from(CHAT_LINK)
                    .join(LINK).on(CHAT_LINK.LINK_ID.eq(LINK.ID))
                    .where(LINK.URL.eq(url)))))
            .execute();
        if (count == 0) {
            throw new NotFoundException("The resource does not exist", "The chat or link was not found");
        }
        return count;
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
