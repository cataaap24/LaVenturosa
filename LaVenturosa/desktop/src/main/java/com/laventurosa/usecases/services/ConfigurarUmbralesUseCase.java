package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;

public class ConfigurarUmbralesUseCase {
    private final UmbralRepository umbralRepository;

    public ConfigurarUmbralesUseCase(UmbralRepository umbralRepository) {
        this.umbralRepository = umbralRepository;
    }

    public OperationResult execute(String punto, String nombreVar, double minC, double minA, double maxA, double maxC) {
        Variable variable = Variable.fromNombre(nombreVar);
        Umbral nuevoUmbral = new Umbral(variable, punto, minC, minA, maxA, maxC);

        umbralRepository.guardar(nuevoUmbral);

        return OperationResult.ok("Umbrales para " + nombreVar + " en " + punto + " actualizados con éxito.");
    }
}