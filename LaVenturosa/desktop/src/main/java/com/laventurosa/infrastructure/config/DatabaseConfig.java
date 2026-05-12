package com.laventurosa.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConfig {

    private static final HikariDataSource dataSource;

        static {
            try (InputStream input = DatabaseConfig.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {

                Properties props = new Properties();
                props.load(input);

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(props.getProperty("db.url"));
                config.setUsername(props.getProperty("db.user"));
                config.setPassword(props.getProperty("db.password"));

                config.setMinimumIdle(2);
                config.setMaximumPoolSize(10);

                // Timeouts
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);

                config.setKeepaliveTime(60000);
                config.setConnectionTestQuery("SELECT 1");

                config.setPoolName("LaVenturosa-Pool");

                dataSource = new HikariDataSource(config);
                System.out.println("[DB] HikariCP pool iniciado correctamente.");

            } catch (Exception e) {
                throw new RuntimeException("[DB] Error iniciando el pool de conexiones: " + e.getMessage(), e);
            }
    }

    private static Connection conexion;

    public static Connection obtenerConexion() throws SQLException {
        return dataSource.getConnection();
    }

    public static void cerrarConexion() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Pool cerrado.");
        }
    }
}