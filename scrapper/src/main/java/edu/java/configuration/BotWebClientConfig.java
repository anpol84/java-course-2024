package edu.java.configuration;

import edu.java.client.BotWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "false")
public class BotWebClientConfig {

    @Value(value = "${api.bot.baseurl}")
    public String botBaseurl;

    @Bean
    public BotWebClient botWebClient() {
        return new BotWebClient(botBaseurl);
    }
}
