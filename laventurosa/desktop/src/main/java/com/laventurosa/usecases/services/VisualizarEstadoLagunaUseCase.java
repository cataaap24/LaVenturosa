package com.laventurosa.usecases.services;

import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.OperationResult;

import java.time.LocalDateTime;

public class VisualizarEstadoLagunaUseCase {
    private MedicionRepository medicionRepository;

    public VisualizarEstadoLagunaUseCase(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public OperationResult execute() {
        LocalDateTime fechaHora = LocalDateTime.now();
        EstadoCriticidad estado = EstadoCriticidad.NORMAL;
        String puntoMonitoreo = "Sur";
        Medicion medicion = new Medicion(Variable.fromNombre("pH"), 10.5, fechaHora, estado, puntoMonitoreo);
        Medicion medicion_guardada = medicionRepository.guardar(medicion);
        if (medicion_guardada == null) {
            return OperationResult.fail("Error al guardar la medicion");
        }
        return OperationResult.ok("Medicion guardada correctamente");
    }
}