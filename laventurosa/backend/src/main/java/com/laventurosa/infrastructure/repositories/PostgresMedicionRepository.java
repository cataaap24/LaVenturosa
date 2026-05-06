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
        return null;
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String puntoMonitoreo) {
        return Optional.empty();
    }

    @Override
    public List<Medicion> obtenerPorRangoYPunto(LocalDateTime desde, LocalDateTime hasta, String puntoMonitoreo) {
        return List.of();
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(LocalDateTime desde, LocalDateTime hasta, String variable) {
        return List.of();
    }

    @Override
    public List<Medicion> obtenerPorRango(LocalDateTime desde, LocalDateTime hasta) {
        return List.of();
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        return List.of();
    }
}
