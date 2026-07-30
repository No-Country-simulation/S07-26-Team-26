package com.ghostload.api.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        String host = environment.getProperty("SPRING_DATASOURCE_HOST");
        String port = environment.getProperty("SPRING_DATASOURCE_PORT");
        String database = environment.getProperty("SPRING_DATASOURCE_DATABASE");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");

        if (host != null) {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();
        }

        return DataSourceBuilder.create()
                .url(environment.getRequiredProperty("spring.datasource.url"))
                .username(username)
                .password(password)
                .build();
    }
}
