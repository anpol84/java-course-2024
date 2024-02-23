package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value(value = "${api.scrapper.baseurl}")
    public String scrapperBaseurl;

    @Bean
    public ScrapperWebClient scrapperWebClient() {
        return new ScrapperWebClient(scrapperBaseurl);
    }
}
