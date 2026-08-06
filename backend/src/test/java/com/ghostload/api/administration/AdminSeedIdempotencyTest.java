package com.ghostload.api.administration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AdminSeedIdempotencyTest {

    private static final String SEED_ID = "8f744cf4-df09-4dc1-985a-a1bb27f7b25f";
    private static final String SEED_EMAIL = "admin@ghostload.local";

    @DynamicPropertySource
    static void dbProperties(DynamicPropertyRegistry registry) throws IOException {
        var env = readEnvFile();
        registry.add("spring.datasource.url", () -> env.get("DB_URL"));
        registry.add("spring.datasource.username", () -> env.get("DB_USERNAME"));
        registry.add("spring.datasource.password", () -> env.get("DB_PASSWORD"));
    }

    private static Map<String, String> readEnvFile() throws IOException {
        Path envFile = Path.of(".env");
        if (!Files.exists(envFile)) {
            Path parent = envFile.toAbsolutePath().getParent();
            throw new IllegalStateException(
                    "No se encontró el archivo .env en " + parent);
        }
        Properties props = new Properties();
        props.load(new StringReader(Files.readString(envFile)));
        Map<String, String> map = new HashMap<>();
        props.stringPropertyNames().forEach(key -> map.put(key, props.getProperty(key)));
        return map;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void seedCreatesSingleAdmin() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, email, role, active FROM admin_users WHERE email = ?",
                SEED_EMAIL);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).get("id").toString()).isEqualTo(SEED_ID);
        assertThat(rows.get(0).get("role").toString()).isEqualTo("ADMIN");
    }

    @Test
    void reApplyingSeedDoesNotDuplicateAdmin() {
        long before = countAdmins();

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO admin_users (id, name, email, password_hash, role, active, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                SEED_ID,
                "Ghost Load Admin",
                SEED_EMAIL,
                "$2a$12$x3FX7w3EcHqbLH5yJX4U3eshlxdegm3f8EjCEZ1tycpcF2Mc2xulu",
                "ADMIN",
                true))
                .isInstanceOf(DataAccessException.class);

        assertThat(countAdmins()).isEqualTo(before);
    }

    private long countAdmins() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM admin_users WHERE email = ?",
                Long.class,
                SEED_EMAIL);
    }
}