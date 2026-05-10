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
                INSERT INTO umbral (variable, punto_monitreo, min_critico, 
                    min_advertencia, max_advertencia, max_critico)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (variable, punto_monitoreo)
                DO UPDATE SET min_critico = EXCLUDED. min_critico,
                              min_advertencia = EXCLUDED.min_advertencia,
                              max_advertencia = EXCLUDED.max_advertencia,
                              max_critico = EXCLUDED.max_critico
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
        SELECT id, variable, punto_monitoreo, min_critico,
               min_advertencia, max_advertencia, max_critico
        FROM umbral 
        WHERE variable = ? AND punto_monitoreo = 'GLOBAL'
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
        SELECT id, variable, punto_monitoreo, min_critico, 
               min_advertencia, max_advertencia, max_critico
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
        String punto = rs.getString("punto_monitoreo");
        return new Umbral(rs.getLong("id"), var,
                "GLOBAL".equals(punto) ? null : punto, // Si es GLOBAL, en Java es null
                rs.getDouble("min_critico"), rs.getDouble("min_advertencia"), rs.getDouble("max_advertencia"), rs.getDouble("max_critico"));
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
