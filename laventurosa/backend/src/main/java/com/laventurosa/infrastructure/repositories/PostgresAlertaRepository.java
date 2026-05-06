package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.*;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.AlertaRepository;

import java.sql.*;
import java.util.*;

public class PostgresAlertaRepository implements AlertaRepository {

    @Override
    public Alerta guardar(Alerta alerta) {
        return null;
    }

    @Override
    public List<Alerta> listarTodas() {
        return List.of();
    }
}
