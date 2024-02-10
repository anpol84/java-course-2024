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
        linkDao.addResource(123456789L,"github.com", "/example");
        linkDao.addResource(123456789L, "stackoverflow.com", "/questions");

        Map<String, Set<String>> resources = linkDao.getResources().get(123456789L);

        assertThat(resources).containsKeys("github.com", "stackoverflow.com");
        assertThat(resources.get("github.com")).containsExactly("/example");
        assertThat(resources.get("stackoverflow.com")).containsExactly("/questions");
    }

    @Test
    void testDeleteResource() {
        LinkDao linkDao = new LinkDao();
        linkDao.addResource(123456789L, "github.com", "/example");
        linkDao.addResource(123456789L, "stackoverflow.com", "/questions");

        assertThat(linkDao.deleteResource(123456789L, "github.com", "/example")).isTrue();
        assertThat(linkDao.deleteResource(123456789L, "stackoverflow.com", "/questions")).isTrue();
        assertThat(linkDao.deleteResource(123456789L, "github.com", "/invalid")).isFalse();

        assertThat(linkDao.getResources().get(123456789L)).isEmpty();
    }
}
