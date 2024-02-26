package edu.java.scrapper;


import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.sql.DataSource;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
public abstract class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    static  {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }


    private static void runMigrations(JdbcDatabaseContainer<?> c) throws SQLException, LiquibaseException {
        String jdbcUrl = c.getJdbcUrl();
        String username = c.getUsername();
        String password = c.getPassword();

        DataSource dataSource = DataSourceBuilder.create()
            .url(jdbcUrl)
            .username(username)
            .password(password)
            .build();


        Database database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        Liquibase liquibase = new liquibase
            .Liquibase("migrations/scheme.sql", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());

    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    public static void main(String[] args) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceBuilder
            .create()
            .url(POSTGRES.getJdbcUrl())
            .username(POSTGRES.getUsername())
            .password(POSTGRES.getPassword())
            .build());

        jdbcTemplate.update("INSERT INTO chat (id) VALUES (?)", 1);

        Integer chatId = jdbcTemplate.queryForObject("SELECT id FROM chat WHERE id = ?", Integer.class, 1);
        assertEquals(1,chatId);

        jdbcTemplate.update("INSERT INTO link (id, url) VALUES (?, ?)", 1,  "http://example.com");

        String url = jdbcTemplate.queryForObject("SELECT url FROM link WHERE id = ?", String.class, 1);
        assertEquals("http://example.com", url);

        jdbcTemplate.update("INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)", 1, 1);
        Integer linkId = jdbcTemplate.queryForObject("SELECT link_id FROM chat_link WHERE chat_id = ?", Integer.class, 1);
        assertEquals(1, linkId);
    }
}
