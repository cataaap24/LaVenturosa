package com.laventurosa.infrastructure.config;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConfig {

    private static final Properties props = new Properties();
    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                System.err.println("[DB] Error: No se encontró application.properties en resources");
            }
        } catch (Exception e) {
            System.err.println("[DB] Error cargando configuración: " + e.getMessage());
        }

        URL = props.getProperty("db.url");
        USUARIO = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    private static Connection conexion;

    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("[DB] Conexión exitosa desde el Desktop a Supabase.");

            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL no encontrado en el proyecto.", e);
            }
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("[DB] Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error al cerrar: " + e.getMessage());
        }
    }
}