package com.laventurosa.usecases.ports;

import com.laventurosa.entities.Umbral;
import java.util.List;
import java.util.Optional;

public interface UmbralRepository {
    Umbral guardar(Umbral umbral);
    Optional<Umbral> obtenerPorPuntoYVariable(String puntoMonitoreo, String nombreVariable);
    List<Umbral> listarTodos();
    List<Umbral> listarPorPunto(String puntoMonitoreo);
}