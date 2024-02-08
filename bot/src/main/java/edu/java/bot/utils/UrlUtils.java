package edu.java.bot.utils;

import java.util.HashSet;
import java.util.Set;

public class UrlUtils {
    private static final int DOMAIN_SEPARATOR = 3;
    private static final int RESOURCE_BEGIN = 4;
    private static final Set<String> DOMAINS;

    static {
        DOMAINS = new HashSet<>();
        DOMAINS.add("github.com");
        DOMAINS.add("stackoverflow.com");
    }

    private UrlUtils() {

    }

    public static boolean isValidUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        String[] parts = url.split("/");
        if (parts.length < RESOURCE_BEGIN) {
            return false;
        }
        String domain = parts[DOMAIN_SEPARATOR - 1];
        String path = getPath(url);
        if (!isValidDomain(domain) || path.contains("?"))  {
            return false;
        }
        return true;
    }

    private static boolean isValidDomain(String domain) {
        return DOMAINS.contains(domain);
    }

    public static String getDomain(String url) {
        String domain = "";
        String[] parts = url.split("/");
        if (parts.length >= DOMAIN_SEPARATOR) {
            domain = parts[0] + "//" + parts[2];
        }
        return domain;
    }

    public static String getPath(String url) {
        String path = "";
        String[] parts = url.split("/");
        if (parts.length >= RESOURCE_BEGIN) {
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = DOMAIN_SEPARATOR; i < parts.length; i++) {
                pathBuilder.append(parts[i]);
                if (i < parts.length - 1) {
                    pathBuilder.append("/");
                }
            }
            path = pathBuilder.toString();
        }
        return path;
    }
}
