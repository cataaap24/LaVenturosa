package com.laventurosa.infrastructure.config;

import java.sql.*;

public class DatabaseConfig {

    private static Connection conexion;

    public static Connection obtenerConexion() { 
        try {
            if (conexion == null || conexion.isClosed()) {
                String envUrl = System.getenv("DATABASE_URL");
                
                if (envUrl == null || envUrl.isBlank()) {
                    throw new RuntimeException("Variable DATABASE_URL no configurada en Render");
                }

                String jdbcUrl = envUrl.replace("postgres://", "jdbc:postgresql://")
                                       .replace("postgresql://", "jdbc:postgresql://");
                if (!jdbcUrl.contains("sslmode")) {
                    jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
                }

                Class.forName("org.postgresql.Driver");
        
                conexion = DriverManager.getConnection(jdbcUrl);
                
                System.out.println("[BD] Conexión establecida con éxito");
            }
            return conexion;
        } catch (Exception e) {
            System.err.println("[BD Error] " + e.getMessage());
            throw new RuntimeException("Error crítico en base de datos: " + e.getMessage(), e);
        }
    }

    public static void cerrarConexion() {
        try { 
            if (conexion != null && !conexion.isClosed()) conexion.close();
        } catch (SQLException e) {
            System.err.println("[BD] Error al cerrar: " + e.getMessage()); 
        }
    }
}
