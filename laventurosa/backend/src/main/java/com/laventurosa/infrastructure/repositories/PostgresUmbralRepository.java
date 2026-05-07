package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.*;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.sql.*;
import java.util.*;

public class PostgresUmbralRepository implements UmbralRepository {

    @Override
    public Umbral guardar(Umbral umbral) {
        /*
         * Implementación de UPSERT: Intenta insertar una nueva configuración de umbral
         * Si ya existe una para esa var en ese punto, actualiza los valores
         * existentes con los nuevos (EXCLUDED) que vienen del formulario
         */
        String sql = """
                INSERT INTO umbral (variable, puntoMonitoreo, minCritico, 
                    minAdvertencia, maxAdvertencia, maxCritico)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (variable, puntoMonitoreo)
                DO UPDATE SET minCritico = EXCLUDED.minCritico,
                              minAdvertencia = EXCLUDED.minAdvertencia,
                              maxAdvertencia = EXCLUDED.maxAdvertencia,
                              maxCritico = EXCLUDED.maxCritico
                RETURNING id
                """;

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, umbral.getVariable().getNombre());
            ps.setString(2, umbral.getPuntoMonitoreo() != null ? umbral.getPuntoMonitoreo() : "GLOBAL");
            ps.setDouble(3, umbral.getMinCritico());
            ps.setDouble(4, umbral.getMinAdvertencia());
            ps.setDouble(5, umbral.getMaxAdvertencia());
            ps.setDouble(6, umbral.getMaxCritico());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Umbral(rs.getLong("id"), umbral.getVariable(), umbral.getPuntoMonitoreo(), umbral.getMinCritico(),
                            umbral.getMinAdvertencia(), umbral.getMaxAdvertencia(), umbral.getMaxCritico());
                }
            }
            throw new RuntimeException("No se obtuvo ID al guardar o actualizar el umbral.");

        } catch (SQLException e) {
            throw new RuntimeException("Error en la base de datos al procesar umbral: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Umbral> obtenerPorVariable(String nombreVariable) {
        String sql = """
        SELECT id, variable, puntoMonitoreo, minCritico,
               minAdvertencia, maxAdvertencia, maxCritico
        FROM umbral WHERE variable = ? AND puntoMonitoreo = 'GLOBAL'
        LIMIT 1
        """;

        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreVariable);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener umbral variable: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Umbral> listarTodos() {
        String sql = """
        SELECT id, variable, puntoMonitoreo, minCritico, 
               minAdvertencia, maxAdvertencia, maxCritico 
        FROM umbral
        """;

        List<Umbral> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar umbrales: " + e.getMessage(), e);
        }

        return lista;
    }
    private Umbral mapear(ResultSet rs) throws SQLException {
        Variable var = resolverVariable(rs.getString("variable"));
        String punto = rs.getString("puntoMonitoreo");
        return new Umbral(rs.getLong("id"), var,
                "GLOBAL".equals(punto) ? null : punto, // Si es GLOBAL, en Java es null
                rs.getDouble("minCritico"), rs.getDouble("minAdvertencia"), rs.getDouble("maxAdvertencia"), rs.getDouble("maxCritico"));
    }
    private Variable resolverVariable(String nombre) {
        Variable variableResultante;
        switch (nombre) {
            case "pH":
                variableResultante = Variable.pH();
                break;
        /*
        case "OxigenoDisuelto":
            variableResultante = Variable.oxigenoDisuelto();
            break;

        case "Temperatura":
            variableResultante = Variable.temperatura();
            break;
        */
            default:
                variableResultante = Variable.pH();
                break;
        }

        return variableResultante;
    }
}
