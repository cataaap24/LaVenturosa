package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.Variable;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.UmbralRepository;
import com.laventurosa.entities.Umbral;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class PostgresUmbralRepository implements UmbralRepository {
    public PostgresUmbralRepository() {}

    @Override
    public Umbral guardar(Umbral umbral) {
        //Integrando UPSERT, permitiendo agregar en caso de que no exista o modificar en caso de que exista
        String sqlInstruction = "INSERT INTO umbral (variable, \"puntoMonitoreo\", \"minCritico\", " +
                "\"minAdvertencia\", \"maxAdvertencia\",\"maxCritico\") VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (variable, punto_monitoreo) " +
                "DO UPDATE SET min_critico = EXCLUDED.min_critico, " +
                               "min_advertencia = EXCLUDED.min_advertencia, " +
                               "max_advertencia = EXCLUDED.max_advertencia, " +
                               "max_critico = EXCLUDED.max_critico " +
                "RETURNING *";
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, umbral.getVariable().getNombre());
            stmt.setString(2, umbral.getPuntoMonitoreo());
            stmt.setDouble(3, umbral.getMinCritico());
            stmt.setDouble(4, umbral.getMinAdvertencia());
            stmt.setDouble(5, umbral.getMaxAdvertencia());
            stmt.setDouble(6, umbral.getMaxCritico());

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()) {
                    return mapearAUmbral(queryResult);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error guardando umbral: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Umbral> obtenerPorVariable(String nombreVariable) {
        String sqlInstruction = "SELECT * FROM umbral " +
                "WHERE variable = ? " +
                "LIMIT 1";

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, nombreVariable);

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()) {
                    return Optional.of(mapearAUmbral(queryResult));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo umbral: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Umbral> listarTodos() {
        String sqlInstruction = "SELECT * FROM umbral";
        List<Umbral> umbrales = new ArrayList<>();
        try (Connection conn = DatabaseConfig.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()) {
                    umbrales.add(mapearAUmbral(queryResult));
                }
                return umbrales;
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo umbrales: " + e.getMessage());
        }
        return umbrales;
    }

    private Umbral mapearAUmbral(ResultSet queryResult) throws SQLException {
        return new Umbral(
                queryResult.getLong("id"),
                Variable.fromNombre(queryResult.getString("variable")),
                queryResult.getString("puntoMonitoreo"),
                queryResult.getDouble("minCritico"),
                queryResult.getDouble("minAdvertencia"),
                queryResult.getDouble("maxAdvertencia"),
                queryResult.getDouble("maxCritico")
        );
    }
}
