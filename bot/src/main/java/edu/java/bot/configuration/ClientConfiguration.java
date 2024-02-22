package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    public ScrapperWebClient scrapperWebClient() {
        return new ScrapperWebClient();
    }
}
