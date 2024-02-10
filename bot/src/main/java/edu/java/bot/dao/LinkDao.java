package edu.java.bot.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class LinkDao {
    private final Map<Long, Map<String, Set<String>>> map;

    public LinkDao() {
        this.map = new HashMap<>();
    }

    public void addResource(Long chatId, String domain, String resource) {
        if (!map.containsKey(chatId)) {
            map.put(chatId, new HashMap<>());
        }
        Map<String, Set<String>> concreteUserMap = map.get(chatId);
        if (!concreteUserMap.containsKey(domain)) {
            concreteUserMap.put(domain, new HashSet<>());
        }
        concreteUserMap.get(domain).add(resource);
    }

    public Map<Long, Map<String, Set<String>>> getResources() {
        return Collections.unmodifiableMap(map);
    }

    public boolean deleteResource(Long chatId, String domain, String resource) {
        if (!map.containsKey(chatId)) {
            return false;
        }

        Map<String, Set<String>> concreteUserMap = map.get(chatId);
        if (!concreteUserMap.containsKey(domain)) {
            return false;
        }

        Set<String> resources = concreteUserMap.get(domain);
        if (!resources.contains(resource)) {
            return false;
        }

        resources.remove(resource);
        if (resources.isEmpty()) {
            concreteUserMap.remove(domain);
        }

        return true;
    }
}
