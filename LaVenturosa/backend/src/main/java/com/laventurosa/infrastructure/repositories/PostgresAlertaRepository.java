package com.laventurosa.infrastructure.repositories;

import java.util.List;

import com.laventurosa.entities.Alerta;
import com.laventurosa.usecases.ports.AlertaRepository;

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
