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
        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD"); // Usamos DB_PASSWORD para coincidir con Render

        HikariConfig config = new HikariConfig();
        
        try {
            // Forzar carga del driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (envUrl != null && !envUrl.isBlank()) {
                config.setJdbcUrl(envUrl);
                config.setUsername(envUser);
                config.setPassword(envPass);
                logger.info("Configurando conexión remota a: {}", envUrl.split("\\?")[0]);
            } else {
                Properties props = new Properties();
                try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (is == null) throw new RuntimeException("No se encontró config.properties");
                    props.load(is);
                    config.setJdbcUrl(props.getProperty("db.url"));
                    config.setUsername(props.getProperty("db.user"));
                    config.setPassword(props.getProperty("db.password"));
                    logger.info("Configurando conexión local desde config.properties");
                }
            }

            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(5); // Reducimos pool para plan gratuito
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            logger.info("Pool de conexiones HikariCP inicializado con éxito.");

        } catch (Exception e) {
            logger.error("ERROR FATAL: No se pudo inicializar la base de datos: {}", e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
