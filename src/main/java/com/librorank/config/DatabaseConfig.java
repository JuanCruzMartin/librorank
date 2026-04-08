package com.librorank.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de configuración para la conexión a la base de datos MySQL usando HikariCP.
 */
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private static final String CONFIG_FILE = "config.properties";
    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                logger.error("No se encontró el archivo {}", CONFIG_FILE);
                throw new RuntimeException("Archivo de configuración no encontrado: " + CONFIG_FILE);
            }
            props.load(is);

            String driver = System.getenv("DB_DRIVER") != null ? System.getenv("DB_DRIVER") : props.getProperty("db.driver");
            String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : props.getProperty("db.url");
            String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("db.user");
            String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : props.getProperty("db.password");

            Class.forName(driver);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            // Ajustes del pool desde properties o valores por defecto
            config.setMaximumPoolSize(Integer.parseInt(System.getenv("DB_POOL_MAX") != null ? System.getenv("DB_POOL_MAX") : props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(Integer.parseInt(System.getenv("DB_POOL_MIN") != null ? System.getenv("DB_POOL_MIN") : props.getProperty("db.pool.minIdle", "2")));
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(30000);

            // Optimizaciones MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            logger.info("HikariCP Connection Pool inicializado desde {}", CONFIG_FILE);

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error al cargar la configuración de la base de datos: {}", e.getMessage(), e);
            throw new RuntimeException("Error de configuración de BD: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error fatal al configurar HikariCP: {}", e.getMessage(), e);
            throw new RuntimeException("Error al inicializar el pool de conexiones: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
