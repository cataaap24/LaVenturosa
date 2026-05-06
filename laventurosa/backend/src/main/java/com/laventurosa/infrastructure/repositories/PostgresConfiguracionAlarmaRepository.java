package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.sql.*;
import java.util.*;

public class PostgresConfiguracionAlarmaRepository implements ConfiguracionAlarmaRepository {

    @Override
    public ConfiguracionAlarma guardar(ConfiguracionAlarma config) {
        return null;
    }

    @Override
    public List<ConfiguracionAlarma> listarTodas() {
        return List.of();
    }

    @Override
    public void eliminar(Long id) {

    }
}
