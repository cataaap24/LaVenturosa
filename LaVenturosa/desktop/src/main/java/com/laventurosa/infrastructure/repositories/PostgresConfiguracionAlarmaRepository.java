package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresConfiguracionAlarmaRepository implements ConfiguracionAlarmaRepository {
    public PostgresConfiguracionAlarmaRepository() {}

    @Override
    public ConfiguracionAlarma guardar(ConfiguracionAlarma config) {
        String sqlInstruction = "INSERT INTO configuracion_alarma (" +
                "email_destinatario, nivel_notificacion, activo) " +
                "VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {

            stmt.setString(1, config.getEmailDestinatario());
            stmt.setString(2, config.getNivelNotificacion().name());
            stmt.setBoolean(3, config.isActivo());

            try (ResultSet queryResult = stmt.executeQuery()) {
                if (queryResult.next()){
                    return mapearAConfiguracion(queryResult, config);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error guardando configuración de alarma: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ConfiguracionAlarma> listarTodas() {
        String sqlInstruction = "SELECT * FROM configuracion_alarma";
        List<ConfiguracionAlarma> configuracionAlarmas = new ArrayList<>();
        try (Connection conn = DatabaseConfig.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {
            try (ResultSet queryResult = stmt.executeQuery()) {
                while (queryResult.next()){
                    configuracionAlarmas.add(mapearAConfiguracion(queryResult));
                }
                return configuracionAlarmas;
            }
        }
        catch (SQLException e) {
            System.err.println("Error guardando medición: " + e.getMessage());
        }
        return configuracionAlarmas;
    }

    @Override
    public void eliminar(Long id) {
        String sqlInstruction = "DELETE FROM configuracion_alarma WHERE id = ?";
        try (Connection conn = DatabaseConfig.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sqlInstruction)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar configuración de alarma: " + e.getMessage());
        }
    }

    private ConfiguracionAlarma mapearAConfiguracion(ResultSet queryResult) throws SQLException {
        return new ConfiguracionAlarma(
                queryResult.getLong("id"),
                queryResult.getString("email_destinatario"),
                ConfiguracionAlarma.NivelNotificacion.valueOf(queryResult.getString("nivel_notificacion")),
                queryResult.getBoolean("activo")
        );
    }

    private ConfiguracionAlarma mapearAConfiguracion(ResultSet queryResult, ConfiguracionAlarma configuracionAlarma) throws SQLException {
        return new ConfiguracionAlarma(
                queryResult.getLong("id"),
                configuracionAlarma.getEmailDestinatario(),
                configuracionAlarma.getNivelNotificacion(),
                configuracionAlarma.isActivo()
        );
    }
}
