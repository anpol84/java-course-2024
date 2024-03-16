package edu.java.configuration;

import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.jooq.JooqChatRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqConfig {
    @Autowired
    private DSLContext dslContext;

    @Bean
    public LinkRepository jooqLinkRepository() {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public ChatRepository jooqChatRepository() {
        return new JooqChatRepository(dslContext);
    }
}


