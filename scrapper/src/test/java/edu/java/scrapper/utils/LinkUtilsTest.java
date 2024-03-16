package edu.java.scrapper.utils;

import edu.java.utils.LinkUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class LinkUtilsTest {
    @Test
    public void extractDomainFromUrlTest() {

        String stackoverflow = "https://stackoverflow.com/some/some";
        String github = "https://github.com/some/some";

        String stackoverflowDomain = LinkUtils.extractDomainFromUrl(stackoverflow);
        String githubDomain = LinkUtils.extractDomainFromUrl(github);

        assertEquals("stackoverflow.com", stackoverflowDomain);
        assertEquals("github.com", githubDomain);
    }
}
