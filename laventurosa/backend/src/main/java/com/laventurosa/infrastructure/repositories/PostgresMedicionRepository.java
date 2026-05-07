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
            INSERT INTO medicion (variable, valor, "fechaHora", estado, "puntoMonitoreo")
            VALUES (?, ?, ?, ?, ?) RETURNING id
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicion.getVariable().getNombre());
            ps.setDouble(2, medicion.getValor());
            ps.setTimestamp(3, Timestamp.valueOf(medicion.getFechaHora()));
            ps.setString(4, medicion.getEstado().name());
            ps.setString(5, medicion.getPuntoMonitoreo());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Medicion(rs.getLong("id"), medicion.getVariable(), medicion.getValor(),
                        medicion.getFechaHora(), medicion.getEstado(), medicion.getPuntoMonitoreo());
            throw new RuntimeException("No se obtuvo ID al guardar medición.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String punto) {
        String sql = """
            SELECT id, variable, valor, "fechaHora", estado, "puntoMonitoreo"
            FROM medicion WHERE "puntoMonitoreo" = ?
            ORDER BY "fechaHora" DESC LIMIT 1
            """;
        // try-with-resources. Cierra conexiones
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, punto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRangoYPunto(LocalDateTime desde, LocalDateTime hasta, String punto) {
        String sql = """
            SELECT id, variable, valor, "fechaHora", estado, "puntoMonitoreo"
            FROM medicion
            WHERE "fechaHora" BETWEEN ? AND ? AND "puntoMonitoreo" = ?
            ORDER BY "fechaHora" ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            ps.setString(3, punto);
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(LocalDateTime desde, LocalDateTime hasta, String variable) {
        String sql = """
            SELECT id, variable, valor, "fechaHora", estado, "puntoMonitoreo"
            FROM medicion
            WHERE "fechaHora" BETWEEN ? AND ? AND variable = ?
            ORDER BY "fechaHora" ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            ps.setString(3, variable);
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerPorRango(LocalDateTime desde, LocalDateTime hasta) {
        String sql = """
            SELECT id, variable, valor, "fechaHora", estado, "puntoMonitoreo"
            FROM medicion WHERE "fechaHora" BETWEEN ? AND ?
            ORDER BY "fechaHora" ASC
            """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(desde));
            ps.setTimestamp(2, Timestamp.valueOf(hasta));
            return listar(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        String sql = """
            SELECT id, variable, valor, "fechaHora", estado, "puntoMonitoreo"
            FROM medicion WHERE "fechaHora" >= NOW() - INTERVAL '1 month'
            ORDER BY "fechaHora" ASC
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
        Variable variable = resolverVariable(rs.getString("variable"));
        return new Medicion(
                rs.getLong("id"), variable, rs.getDouble("valor"),
                rs.getTimestamp("fechaHora").toLocalDateTime(),
                EstadoCriticidad.valueOf(rs.getString("estado")),
                rs.getString("puntoMonitoreo"));
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
