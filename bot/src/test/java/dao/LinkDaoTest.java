package dao;

import static org.assertj.core.api.Assertions.assertThat;
import edu.java.bot.dao.LinkDao;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Set;

public class LinkDaoTest {

    @Test
    void testAddResourceAndGetResources() {
        LinkDao linkDao = new LinkDao();
        linkDao.addResource("github.com", "/example");
        linkDao.addResource("stackoverflow.com", "/questions");

        Map<String, Set<String>> resources = linkDao.getResources();

        assertThat(resources).containsKeys("github.com", "stackoverflow.com");
        assertThat(resources.get("github.com")).containsExactly("/example");
        assertThat(resources.get("stackoverflow.com")).containsExactly("/questions");
    }

    @Test
    void testDeleteResource() {
        LinkDao linkDao = new LinkDao();
        linkDao.addResource("github.com", "/example");
        linkDao.addResource("stackoverflow.com", "/questions");

        assertThat(linkDao.deleteResource("github.com", "/example")).isTrue();
        assertThat(linkDao.deleteResource("stackoverflow.com", "/questions")).isTrue();
        assertThat(linkDao.deleteResource("github.com", "/invalid")).isFalse();

        assertThat(linkDao.getResources()).isEmpty();
    }
}
