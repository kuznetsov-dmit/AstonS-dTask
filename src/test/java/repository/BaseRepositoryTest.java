package repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.DatabaseConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@Testcontainers
public abstract class BaseRepositoryTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("library_test")
            .withUsername("test")
            .withPassword("test");

    private static HikariDataSource dataSource;

    @BeforeAll
    static void initDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);
    }

    @BeforeEach
    void setUp() {
        try {
            executeSqlScript("/schema.sql");
            executeSqlScript("/test-data.sql");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize test database", e);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    private void executeSqlScript(String scriptPath) {
        try {
            String sql = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream(scriptPath)))
                    .lines()
                    .collect(Collectors.joining("\n"));

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL script: " + scriptPath, e);
        }
    }
}
