package com.laventurosa.usecases.ports;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.MedicionDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 *  Optional para indica explícitamente que el punto de monitoreo 
 * podría no tener ninguna medición registrada aún. Esto obliga a manejar el caso de "valor no encontrado, evitando errores de puntero nulo (NullPointerException).
 */

public interface MedicionRepository {
    Medicion guardar(Medicion medicion);
    Optional<Medicion> obtenerUltimaPorPunto(String puntoMonitoreo);
    List<Medicion> obtenerPorRangoYPunto(OffsetDateTime desde, OffsetDateTime hasta, String puntoMonitoreo);
    List<Medicion> obtenerPorRangoYVariable(OffsetDateTime desde, OffsetDateTime hasta, String variable);
    List<Medicion> obtenerPorRango(OffsetDateTime desde, OffsetDateTime hasta);
    List<Medicion> obtenerUltimoMes();
}

