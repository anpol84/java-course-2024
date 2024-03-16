package edu.java.service.updater;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LinkHolder {
    private final Map<String, LinkUpdater> updaterMap;

    @Autowired
    public LinkHolder(List<LinkUpdater> updaters) {
        updaterMap = updaters.stream().collect(Collectors.toMap(LinkUpdater::getDomain, Function.identity()));
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return updaterMap.get(domain);
    }
}
