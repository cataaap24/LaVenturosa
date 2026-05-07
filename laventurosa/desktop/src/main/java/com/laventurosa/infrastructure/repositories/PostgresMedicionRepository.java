package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.infrastructure.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PostgresMedicionRepository implements MedicionRepository {
    public PostgresMedicionRepository() {}

    @Override
    public Medicion guardar(Medicion medicion) {
        String sqlInstruction = "INSERT INTO Medicion (variable, valor, fechaHora, estado, puntoMonitoreo) " +
        "VALUES (?, ?, ?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseConfig.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, medicion.getVariable().getNombre());
            stmt.setDouble(2, medicion.getValor());
            stmt.setObject(3, medicion.getFechaHora());
            stmt.setString(4, medicion.getEstado().name());
            stmt.setString(5, medicion.getPuntoMonitoreo());

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()) {
                    return mapearAMedicion(queryResult);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error guardando medición: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String puntoMonitoreo) {
        String sqlInstruction = "SELECT * FROM Medicion " +
                "WHERE puntoMonitoreo = ? " +
                "ORDER BY fechaHora DESC " +
                "LIMIT 1;";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, puntoMonitoreo);

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
    public List<Medicion> obtenerPorRangoYPunto(LocalDateTime desde, LocalDateTime hasta, String puntoMedicion) {
        String sqlInstruction = "SELECT * FROM Medicion " +
                "WHERE puntoMonitoreo = ? AND fechaHora BETWEEN ? AND ? " +
                "ORDER BY fechaHora DESC";

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

        return null;
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(LocalDateTime desde, LocalDateTime hasta, String variable) {
        String sqlInstruction = "SELECT * FROM Medicion " +
                "WHERE variable = ? AND fechaHora BETWEEN ? AND ? " +
                "ORDER BY fechaHora DESC";

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

        return null;
    }

    @Override
    public List<Medicion> obtenerPorRango(LocalDateTime desde, LocalDateTime hasta) {
        String sqlInstruction = "SELECT * FROM Medicion " +
                "WHERE fechaHora BETWEEN ? AND ? " +
                "ORDER BY fechaHora DESC";
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

        return null;
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        String sqlInstruction = "SELECT * FROM Medicion " +
                "WHERE fechaHora >= CURRENT_DATE - INTERVAL '1 month' " +
                "ORDER BY fechaHora DESC";

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

        return null;
    }

    private Medicion mapearAMedicion(ResultSet queryResult) throws SQLException {
        return new Medicion(
                queryResult.getLong("id"),
                Variable.fromNombre(queryResult.getString("variable")),
                queryResult.getDouble("valor"),
                queryResult.getObject("fechaHora", LocalDateTime.class),
                EstadoCriticidad.valueOf(queryResult.getString("estado")),
                queryResult.getString("punto_monitoreo")
        );
    }
}
