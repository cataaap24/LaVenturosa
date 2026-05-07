package com.laventurosa.usecases.ports;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.MedicionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *  Optional para indica explícitamente que el punto de monitoreo 
 * podría no tener ninguna medición registrada aún. Esto obliga a manejar el caso de "valor no encontrado, evitando errores de puntero nulo (NullPointerException).
 */

public interface MedicionRepository {
    Medicion guardar(Medicion medicion);
    Optional<Medicion> obtenerUltimaPorPunto(String puntoMonitoreo);
    List<Medicion> obtenerPorRangoYPunto(LocalDateTime desde, LocalDateTime hasta, String puntoMonitoreo);
    List<Medicion> obtenerPorRangoYVariable(LocalDateTime desde, LocalDateTime hasta, String variable);
    List<Medicion> obtenerPorRango(LocalDateTime desde, LocalDateTime hasta);
    List<Medicion> obtenerUltimoMes();
}

