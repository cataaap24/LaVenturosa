package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;

import java.util.Optional;

public class VisualizarEstadoLagunaUseCase {
    private final MedicionRepository medicionRepository;

    public VisualizarEstadoLagunaUseCase(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public OperationResult<EstadoLagunaDTO> execute(String puntoMonitoreo) {
        Optional<Medicion> medicionOpt = medicionRepository.obtenerUltimaPorPunto(puntoMonitoreo);

        if (medicionOpt.isEmpty()) {
            return OperationResult.fail("Sin datos disponibles para el punto: " + puntoMonitoreo);
        }

        Medicion medicion = medicionOpt.get();

        EstadoLagunaDTO informe = new EstadoLagunaDTO(
                medicion.getValor(),
                medicion.getFechaHora(),
                medicion.getPuntoMonitoreo(),
                medicion.getEstado().name()
        );

        imprimirLog(informe);

        return OperationResult.ok("Estado de la laguna recuperado exitosamente", informe);
    }

    private void imprimirLog(EstadoLagunaDTO informe) {
        System.out.println("[CONSULTA] Visualizando " + informe.getPuntoMonitoreo() +
                " | Valor: " + informe.getValor() +
                " | Estado: " + informe.getEstado());
    }
}