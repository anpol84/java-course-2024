package edu.java.configuration;

import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DSLContextConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DSLContext dslContext() {
        return DSL.using(dataSource, SQLDialect.POSTGRES);
    }
}
