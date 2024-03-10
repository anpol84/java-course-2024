package edu.java.utils;


public class LinkUtils {

    private final static int PROTOCOL_SYMBOLS = 3;
    private final static String SEPARATOR = "://";
    private final static String STACKOVERFLOW_REGEX = "https://stackoverflow\\.com/questions/\\d+";
    private final static String GITHUB_REGEX = "https://github\\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+";

    private LinkUtils() {}

    public static boolean validateLink(String url) {
        if (!url.matches(STACKOVERFLOW_REGEX) && !url.matches(GITHUB_REGEX)) {
            return false;
        }
        return true;
    }

    public static String extractDomainFromUrl(String url) {
        String domain = "";
        String newUrl = url;
        if (url.contains(SEPARATOR)) {
            newUrl = url.substring(url.indexOf(SEPARATOR) + PROTOCOL_SYMBOLS);
            int index = newUrl.indexOf("/");
            if (index != -1) {
                domain = newUrl.substring(0, index);
            }
        }
        return domain;
    }

    public static String[] extractGithubInfoFromUrl(String url) {
        String[] parts = url.split("/");
        String repositoryName = parts[parts.length - 1];
        String accountName = parts[parts.length - 2];
        return new String[]{repositoryName, accountName};
    }

    public static String extractStackOverflowInfoFromUrl(String url) {
        String[] args = url.split("/");
        return args[args.length - 1];
    }
}
