// Aquí se estaba violando principio de Clean Arch - retornaba entidad directamente a UI
package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;

import java.time.OffsetDateTime;
import java.util.List;

public class ConsultarHistorialUseCase {

    private final MedicionRepository repository;

    public ConsultarHistorialUseCase(MedicionRepository repository) {
        this.repository = repository;
    }

    public OperationResult<List<MedicionDTO>> execute(OffsetDateTime desde, OffsetDateTime hasta) {
        if (desde == null || hasta == null){
            return OperationResult.fail("Las fechas de inicio y fin son obligatorias.");

        }
        if (desde.isAfter(hasta)){
            return OperationResult.fail("La fecha de inicio debe ser anterior a la fecha fin.");
        }

        List<Medicion> mediciones = repository.obtenerPorRango(desde, hasta);

        List<MedicionDTO> dtos = mediciones.stream().map(m -> new MedicionDTO(
                        m.getId(),
                        m.getVariable().getNombre(),
                        m.getVariable().getUnidad(),
                        m.getValor(),
                        m.getFechaHora(),
                        m.getEstado(),
                        m.getPuntoMonitoreo())).toList();

        return OperationResult.ok("Se encontraron " + dtos.size() + " registros.", dtos);
    }
}
