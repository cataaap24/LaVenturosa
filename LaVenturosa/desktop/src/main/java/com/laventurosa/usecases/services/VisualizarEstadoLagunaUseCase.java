package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import java.util.ArrayList;
import java.util.List;

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

        /*
            List<Medicion> recientes = medicionRepository.obtenerPorRangoYPunto(
            medicion.getFechaHora().minusHours(48),
            medicion.getFechaHora(),
            puntoMonitoreo);
            
            private static final int PUNTOS_GRAFICA = 20; // fuera del método --> en el UC
            int inicio = Math.max(0, recientes.size() - PUNTOS_GRAFICA);
            List<MedicionDTO> historialDTO = new ArrayList<>();
            for (int i = inicio; i < recientes.size(); i++) {
                Medicion med = recientes.get(i);
                
                historialDTO.add(new MedicionDTO(
                    med.getId(),
                    med.getVariable().getNombre(),
                    med.getVariable().getUnidad(),
                    med.getValor(),
                    med.getFechaHora(),
                    med.getEstado(),
                    med.getPuntoMonitoreo()
                ));
            }

            return OperationResult.ok("Ok", new EstadoLagunaDTO(
            m.getValor(),
            m.getVariable().getUnidad(),
            m.getEstado(),
            m.getFechaHora(),
            m.getPuntoMonitoreo(),
            historialDTO));
        */
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
