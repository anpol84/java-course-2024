package edu.java.configuration;

import edu.java.service.updater.GithubLinkUpdater;
import edu.java.service.updater.LinkUpdater;
import edu.java.service.updater.StackOverflowLinkUpdater;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UpdaterConfig {

    @Bean
    public Map<String, LinkUpdater> updaterMap(GithubLinkUpdater githubLinkUpdater,
        StackOverflowLinkUpdater stackOverflowLinkUpdater) {
        Map<String, LinkUpdater> map = new HashMap<>();
        map.put(githubLinkUpdater.getDomain(), githubLinkUpdater);
        map.put(stackOverflowLinkUpdater.getDomain(), stackOverflowLinkUpdater);
        return map;
    }
}
