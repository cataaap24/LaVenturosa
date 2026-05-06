package com.laventurosa.infrastructure.repositories;

import com.laventurosa.entities.*;
import com.laventurosa.infrastructure.config.DatabaseConfig;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.sql.*;
import java.util.*;

public class PostgresUmbralRepository implements UmbralRepository {
    @Override
    public Umbral guardar(Umbral umbral) {
        return null;
    }

    @Override
    public Optional<Umbral> obtenerPorVariable(String nombreVariable) {
        return Optional.empty();
    }

    @Override
    public List<Umbral> listarTodos() {
        return List.of();
    }
}
