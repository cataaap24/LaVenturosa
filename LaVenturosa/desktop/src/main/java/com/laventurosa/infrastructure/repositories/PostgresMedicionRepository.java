package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.infrastructure.config.DatabaseConfig;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;

public class PostgresMedicionRepository implements MedicionRepository {
    public PostgresMedicionRepository() {}

    @Override
    public Medicion guardar(Medicion medicion) {
        String sqlInstruction = "INSERT INTO medicion (variable, valor, \"fecha_hora\", estado, \"punto_monitoreo\") " +
        "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, medicion.getVariable().getNombre());
            stmt.setDouble(2, medicion.getValor());
            stmt.setObject(3, medicion.getFechaHora());
            stmt.setString(4, medicion.getEstado().name());
            stmt.setString(5, medicion.getPuntoMonitoreo());

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()) {
                    return mapearAMedicion(queryResult, medicion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error guardando medición: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String punto_monitoreo) {
        String sqlInstruction = "SELECT * FROM medicion " +
                "WHERE punto_monitoreo = ? " +
                "ORDER BY fecha_hora DESC " +
                "LIMIT 1;";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, punto_monitoreo);

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()) {
                    return Optional.of(mapearAMedicion(queryResult));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo medicion: " + e.getMessage());
        }

        return Optional.empty();
    }
    @Override
    public List<Medicion> obtenerPorRangoYPunto(OffsetDateTime desde, OffsetDateTime hasta, String puntoMedicion) {
        String sqlInstruction = "SELECT * FROM medicion " +
                "WHERE punto_monitoreo = ? AND fecha_hora BETWEEN ? AND ? " +
                "ORDER BY fecha_hora DESC";

        List<Medicion> mediciones = new ArrayList<>();

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, puntoMedicion);
            stmt.setObject(2, desde);
            stmt.setObject(3, hasta);

            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    mediciones.add(mapearAMedicion(queryResult));
                }
                return mediciones;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo mediciones: " + e.getMessage());
        }

        return mediciones;
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(OffsetDateTime desde, OffsetDateTime hasta, String variable) {
        String sqlInstruction = "SELECT * FROM medicion " +
                "WHERE variable = ? AND fecha_hora BETWEEN ? AND ? " +
                "ORDER BY fecha_hora DESC";

        List<Medicion> mediciones = new ArrayList<>();

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, variable);
            stmt.setObject(2, desde);
            stmt.setObject(3, hasta);

            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    mediciones.add(mapearAMedicion(queryResult));
                }
                return mediciones;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo mediciones: " + e.getMessage());
        }

        return mediciones;
    }

    @Override
    public List<Medicion> obtenerPorRango(OffsetDateTime desde, OffsetDateTime hasta) {
        String sqlInstruction = "SELECT * FROM medicion " +
                "WHERE fecha_hora BETWEEN ? AND ? " +
                "ORDER BY fecha_hora DESC";
        List<Medicion> mediciones = new ArrayList<>();

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setObject(1, desde);
            stmt.setObject(2, hasta);

            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    mediciones.add(mapearAMedicion(queryResult));
                }
                return mediciones;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo mediciones: " + e.getMessage());
        }

        return mediciones;
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        String sqlInstruction = "SELECT * FROM medicion " +
                "WHERE fecha_hora >= CURRENT_DATE - INTERVAL '1 month' " +
                "ORDER BY fecha_hora DESC";

        List<Medicion> mediciones = new ArrayList<>();
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    mediciones.add(mapearAMedicion(queryResult));
                }
                return mediciones;
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo mediciones: " + e.getMessage());
        }

        return mediciones;
    }

    private Medicion mapearAMedicion(ResultSet queryResult, Medicion medicion) throws SQLException {
        return new Medicion(
                queryResult.getLong("id"),
                medicion.getVariable(),
                medicion.getValor(),
                medicion.getFechaHora(),
                medicion.getEstado(),
                medicion.getPuntoMonitoreo()
        );
    }

    private Medicion mapearAMedicion(ResultSet queryResult) throws SQLException {
        return new Medicion(
                queryResult.getLong("id"),
                Variable.fromNombre(queryResult.getString("variable")),
                queryResult.getDouble("valor"),
                queryResult.getObject("fecha_hora", OffsetDateTime.class),
                EstadoCriticidad.valueOf(queryResult.getString("estado")),
                queryResult.getString("punto_monitoreo")
        );
    }
}
