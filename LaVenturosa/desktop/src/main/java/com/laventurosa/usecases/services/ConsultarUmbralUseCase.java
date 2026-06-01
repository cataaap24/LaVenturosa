package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.UmbralDTO;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.util.Optional;


public class ConsultarUmbralUseCase {
    private final UmbralRepository umbralRepository;

    public ConsultarUmbralUseCase(UmbralRepository umbralRepository) {
        this.umbralRepository = umbralRepository;
    }

    public OperationResult<UmbralDTO> execute(String punto, String nombreVar) {


        Optional<Umbral> umbralOpt = umbralRepository.obtenerPorPuntoYVariable(punto, nombreVar);

        if (umbralOpt.isEmpty()) {
            return OperationResult.fail("No se encontró el umbral para el punto: " + punto + " y variable: " + nombreVar);
        }

        Umbral umbral = umbralOpt.get();

        UmbralDTO dto = new UmbralDTO(
                umbral.getId(),
                umbral.getVariable().getNombre(),
                umbral.getPuntoMonitoreo(),
                umbral.getMinCritico(),
                umbral.getMinAdvertencia(),
                umbral.getMaxAdvertencia(),
                umbral.getMaxCritico()
        );

        return OperationResult.ok("Umbral consultado exitosamente", dto);
    }

}
