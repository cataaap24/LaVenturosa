package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.*;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.MedicionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class PostgresMedicionRepository implements MedicionRepository {

    @Override
    public Medicion guardar(Medicion medicion) {
        String sql = """
            INSERT INTO medicion (variable, valor, fecha_hora, estado, punto_monitoreo)
            VALUES (?, ?, ?, ?, ?) RETURNING id, variable, valor, fecha_hora, estado, punto_monitoreo
            """;
    
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, medicion.getVariable().getNombre());
            ps.setDouble(2, medicion.getValor());
            ps.setObject(3, medicion.getFechaHora()); // Uso de setObject para fecha
            ps.setString(4, medicion.getEstado().name());
            ps.setString(5, medicion.getPuntoMonitoreo());
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar en Postgres: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String punto) {
        String sql = """
            SELECT id, variable, valor, fecha_hora, estado, punto_monitoreo
            FROM medicion WHERE punto_monitoreo = ?
            ORDER BY fecha_hora DESC LIMIT 1
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, punto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRangoYPunto(LocalDateTime desde, LocalDateTime hasta, String punto) {
        String sql = """
            SELECT id, variable, valor, fecha_hora, estado, punto_monitoreo
            FROM medicion
            WHERE fecha_hora BETWEEN ? AND ? AND punto_monitoreo = ?
            ORDER BY fecha_hora ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            ps.setString(3, punto);
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(LocalDateTime desde, LocalDateTime hasta, String variable) {
        String sql = """
            SELECT id, variable, valor, fecha_hora, estado, punto_monitoreo
            FROM medicion
            WHERE fecha_hora BETWEEN ? AND ? AND variable = ?
            ORDER BY fecha_hora ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            ps.setString(3, variable);
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRango(LocalDateTime desde, LocalDateTime hasta) {
        String sql = """
            SELECT id, variable, valor, fecha_hora, estado, punto_monitoreo
            FROM medicion WHERE fecha_hora BETWEEN ? AND ?
            ORDER BY fecha_hora ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        String sql = """
            SELECT id, variable, valor, fecha_hora, estado, punto_monitoreo
            FROM medicion WHERE fecha_hora >= NOW() - INTERVAL '1 month'
            ORDER BY fecha_hora ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Medicion> listar(PreparedStatement ps) throws SQLException {
        List<Medicion> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Medicion mapear(ResultSet rs) throws SQLException {
        return new Medicion(
                rs.getLong("id"),
                resolverVariable(rs.getString("variable")),
                rs.getDouble("valor"),
                rs.getObject("fecha_hora", LocalDateTime.class), 
                EstadoCriticidad.valueOf(rs.getString("estado")),
                rs.getString("punto_monitoreo")
        );
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
