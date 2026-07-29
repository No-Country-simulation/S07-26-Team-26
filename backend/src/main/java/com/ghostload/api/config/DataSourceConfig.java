package com.ghostload.api.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        String host = System.getenv("SPRING_DATASOURCE_HOST");
        String port = System.getenv("SPRING_DATASOURCE_PORT");
        String database = System.getenv("SPRING_DATASOURCE_DATABASE");
        String username = System.getenv("SPRING_DATASOURCE_USERNAME");
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");

        if (host != null) {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            return DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();
        }

        
        return DataSourceBuilder.create()
                .url(System.getenv("SPRING_DATASOURCE_URL"))
                .username(System.getenv("SPRING_DATASOURCE_USERNAME"))
                .password(System.getenv("SPRING_DATASOURCE_PASSWORD"))
                .build();
    }
}
