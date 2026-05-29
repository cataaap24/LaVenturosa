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
        Umbral nuevoUmbral;
        try {
            Variable variable = Variable.fromNombre(nombreVar);

            nuevoUmbral = new Umbral(variable, punto, minC, minA, maxA, maxC);
            } catch (IllegalArgumentException | NullPointerException e) {
            // Logica de negocio
            return OperationResult.fail("Datos de configuración inválidos: " + e.getMessage());
        }
        // Persistencia fuera del catch de negocio
        try {
            umbralRepository.guardar(nuevoUmbral);
            return OperationResult.ok("Umbrales actualizados con éxito.");
        } catch (Exception e) {
            return OperationResult.fail("Error: No se pudo guardar la configuración.");
        }
    }
}
