package edu.java.service.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LinkHolder {
    private final Map<String, LinkUpdater> updaterMap;

    @Autowired
    public LinkHolder(List<LinkUpdater> updaters) {
        updaterMap = new HashMap<>();
        for (LinkUpdater linkUpdater : updaters) {
            updaterMap.put(linkUpdater.getDomain(), linkUpdater);
        }
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return updaterMap.get(domain);
    }
}
