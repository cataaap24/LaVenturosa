package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.sql.*;
import java.util.*;

public class PostgresConfiguracionAlarmaRepository implements ConfiguracionAlarmaRepository {

    @Override
    public ConfiguracionAlarma guardar(ConfiguracionAlarma config) {
        String sql = """
                        INSERT INTO configuracion_alarma (email_destinatario, nivel_notificacion, activo) VALUES (?, ?, ?)
                       RETURNING id
                       """;
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // mapear objetos java a SQL
            ps.setString(1, config.getEmailDestinatario());
            ps.setString(2, config.getNivelNotificacion().name());
            ps.setBoolean(3, config.isActivo());
            ResultSet rs = ps.executeQuery(); // devuelve el ID generado
            if (rs.next()){
                return new ConfiguracionAlarma(rs.getLong("id"), config.getEmailDestinatario(), config.getNivelNotificacion(), config.isActivo());
            }
            throw new RuntimeException("No se obtuvo ID.");
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
    }

    @Override
    public List<ConfiguracionAlarma> listarTodas() {
        return List.of();
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM configuracion_alarma WHERE id = ?";
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id); ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
