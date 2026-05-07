package com.laventurosa.infrastructure.config;

import java.sql.*;
import java.net.URI;

public class DatabaseConfig {

    private static Connection conexion;

    public static Connection obtenerConexion() { 
        try {
            if (conexion == null || conexion.isClosed()) {
                String envUrl = System.getenv("DATABASE_URL");
                
                if (envUrl == null || envUrl.isBlank()) {
                    throw new RuntimeException("DATABASE_URL no configurada.");
                }

                URI dbUri = new URI(envUrl);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(dbUrl, username, password);
                
                System.out.println("[BD] ¡Conexión exitosa a la laguna!");
            }
            return conexion;
        } catch (Exception e) {
            System.err.println("[BD Error Real] " + e.getMessage());
            throw new RuntimeException("Error en BD: " + e.getMessage(), e);
        }
    }

    public static void cerrarConexion() {
        try { 
            if (conexion != null && !conexion.isClosed()) conexion.close();
        } catch (SQLException e) {
            System.err.println("[BD] " + e.getMessage()); 
        }
    }
}
