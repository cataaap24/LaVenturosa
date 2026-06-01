package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

public class EliminarConfiguracionAlarmaUseCase {
    private final ConfiguracionAlarmaRepository configuracionAlarmaRepository;

    public EliminarConfiguracionAlarmaUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    public OperationResult<ConfiguracionAlarma> execute(String email) {
        if (email == null || email.isBlank()) {
            return OperationResult.fail("Campos vacíos");
        }
        try {
            ConfiguracionAlarma config = configuracionAlarmaRepository.obtenerConfiguracionAlarma(email);
            configuracionAlarmaRepository.eliminar(config.getId());
            return OperationResult.ok("Correo eliminado correctamente");
            
        } catch (Exception e) {
            return OperationResult.fail("Error al eliminar el correo: " + e.getMessage());
        }
    }
}
