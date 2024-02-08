package utlis;

import static org.assertj.core.api.Assertions.assertThat;
import edu.java.bot.utils.UrlUtils;
import org.junit.jupiter.api.Test;
public class UrlUtilsTest {

    @Test
    void testValidUrl() {
        assertThat(UrlUtils.isValidUrl("https://github.com/example")).isTrue();
        assertThat(UrlUtils.isValidUrl("https://github.com/sanyarnd/tinkoff-java-course-2023/")).isTrue();
        assertThat(UrlUtils.isValidUrl("https://stackoverflow.com/questions/1642028/what-is-the-operator-in-c"))
            .isTrue();
        assertThat(UrlUtils.isValidUrl("http://stackoverflow.com/questions")).isTrue();
    }

    @Test
    void testInvalidUrl() {
        assertThat(UrlUtils.isValidUrl("ftp://github.com/example")).isFalse();
        assertThat(UrlUtils.isValidUrl("https://invalid.com")).isFalse();
        assertThat(UrlUtils.isValidUrl("https://stackoverflow.com/search?q=unsupported%20link")).isFalse();
    }

    @Test
    void testGetDomain() {
        assertThat(UrlUtils.getDomain("https://github.com/example")).isEqualTo("https://github.com");
        assertThat(UrlUtils.getDomain("http://stackoverflow.com/questions"))
            .isEqualTo("http://stackoverflow.com");
    }

    @Test
    void testGetPath() {
        assertThat(UrlUtils.getPath("https://github.com/example")).isEqualTo("example");
        assertThat(UrlUtils.getPath("http://stackoverflow.com/questions")).isEqualTo("questions");
    }
}
