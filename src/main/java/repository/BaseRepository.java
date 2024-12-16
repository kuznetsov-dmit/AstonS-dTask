package repository;

import config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public abstract class BaseRepository {
    protected final DataSource dataSource;

    protected BaseRepository() {
        this.dataSource = DatabaseConfig.getDataSource();
    }
    protected BaseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
