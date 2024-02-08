package edu.java.bot.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinkDao {
    private final Map<String, Set<String>> map;

    public LinkDao() {
        this.map = new HashMap<>();
    }

    public void addResource(String domain, String resource) {
        if (!map.containsKey(domain)) {
            map.put(domain, new HashSet<>());
        }
        map.get(domain).add(resource);
    }

    public Map<String, Set<String>> getResources() {
        return Collections.unmodifiableMap(map);
    }

    public boolean deleteResource(String domain, String resource) {
        if (map.containsKey(domain)) {
            Set<String> resources = map.get(domain);
            if (resources.contains(resource)) {
                resources.remove(resource);
                if (resources.isEmpty()) {
                    map.remove(domain);
                }
                return true;
            }
        }
        return false;
    }
}
