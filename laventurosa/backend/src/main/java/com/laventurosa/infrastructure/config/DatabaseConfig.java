package com.laventurosa.infrastructure.config;

import java.sql.*;

public class DatabaseConfig {

    private static final String URL;
    private static final String USUARIO;
    private static final String PASSWORD;

    static {
        String envUrl = System.getenv("DATABASE_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            // Normalizar prefijo
            envUrl = envUrl.replace("postgres://", "jdbc:postgresql://").replace("postgresql://", "jdbc:postgresql://");
            // Extraer user:pass@host/db
            String sinJdbc = envUrl.replace("jdbc:postgresql://", "");
            String userPass = sinJdbc.substring(0, sinJdbc.indexOf("@"));
            String hostDb = sinJdbc.substring(sinJdbc.indexOf("@") + 1);
            USUARIO  = userPass.substring(0, userPass.indexOf(":"));
            PASSWORD = userPass.substring(userPass.indexOf(":") + 1);
            URL = "jdbc:postgresql://" + hostDb + "?sslmode=require";
        } else {
            URL = "jdbc:postgresql://localhost:5432/laventurosa_db";
            USUARIO = "postgres";
            PASSWORD = "password_local"; // configurar
        }
    }

    private static Connection conexion;

    public static Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                inicializarEsquema(conexion);
            }
            return conexion;
        } catch (Exception e) {
            throw new RuntimeException("Fallo en la base de datos de la laguna", e);
        }
    }

    private static void inicializarEsquema(Connection conn) throws SQLException {
        String[] sqls = {
                /*
                "CREATE TABLE IF NOT EXISTS medicion ()",
                "CREATE TABLE IF NOT EXISTS umbral ()"
                */
        };

        try (Statement st = conn.createStatement()) {
            for (String sql : sqls) {
                st.execute(sql);
            }
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
