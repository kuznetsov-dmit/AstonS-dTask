package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream is = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("database.properties")) {
                props.load(is);
            }

            HikariConfig config = new HikariConfig();

            // Сначала пробуем взять из переменных окружения
            String dbUrl = System.getenv("DB_URL");
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");

            // Если нет в окружении - берем из properties
            config.setJdbcUrl(dbUrl != null ? dbUrl : props.getProperty("db.url"));
            config.setUsername(dbUsername != null ? dbUsername : props.getProperty("db.username"));
            config.setPassword(dbPassword != null ? dbPassword : props.getProperty("db.password"));

            // Остальные настройки из properties
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.size")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connection.timeout")));

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке конфигурации базы данных", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    private DatabaseConfig() {
    }
}
