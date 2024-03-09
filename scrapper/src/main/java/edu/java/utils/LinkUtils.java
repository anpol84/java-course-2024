package edu.java.utils;

public class LinkUtils {

    private final static int PROTOCOL_SYMBOLS = 8;

    private LinkUtils() {}

    public static String extractDomainFromUrl(String url) {
        String domain = "";
        String newUrl = url;
        if (url.startsWith("https://")) {
            newUrl = url.substring(PROTOCOL_SYMBOLS);
            int index = newUrl.indexOf("/");
            if (index != -1) {
                domain = newUrl.substring(0, index);
            }
        }
        return domain;
    }
}
