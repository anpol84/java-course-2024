package edu.java.configuration;

import edu.java.repository.ChatRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.jpa.JpaChatRepository;
import edu.java.repository.jpa.JpaChatRepositoryInterface;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaLinkRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaConfig {
    @Autowired
    private  JpaLinkRepositoryInterface jpaLinkRepositoryInterface;

    @Autowired
    private JpaChatRepositoryInterface jpaChatRepositoryInterface;

    @Bean
    public LinkRepository jpaLinkRepository() {

        return new JpaLinkRepository(jpaLinkRepositoryInterface);
    }

    @Bean
    public ChatRepository jpaChatRepository() {
        return new JpaChatRepository(jpaChatRepositoryInterface);
    }
}
