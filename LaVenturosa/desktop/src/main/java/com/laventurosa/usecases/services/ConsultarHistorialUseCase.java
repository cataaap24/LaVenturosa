package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultarHistorialUseCase {
    private final MedicionRepository repository;

    public ConsultarHistorialUseCase(MedicionRepository repository) {
        this.repository = repository;
    }

    public List<Medicion> execute(OffsetDateTime desde, OffsetDateTime hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            return new ArrayList<>();
        }

        return repository.obtenerPorRango(desde, hasta);
    }
}