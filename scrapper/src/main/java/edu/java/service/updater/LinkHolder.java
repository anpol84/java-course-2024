package edu.java.service.updater;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class LinkHolder {
    private final Map<String, LinkUpdater> updaterMap;

    @Autowired
    public LinkHolder(@Qualifier("updaterMap") Map<String, LinkUpdater> updaterMap) {
        this.updaterMap = updaterMap;
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return updaterMap.get(domain);
    }
}
