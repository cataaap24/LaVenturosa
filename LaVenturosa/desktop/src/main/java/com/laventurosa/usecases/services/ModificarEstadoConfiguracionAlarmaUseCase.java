package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.ConfiguracionAlarmaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

public class ModificarEstadoConfiguracionAlarmaUseCase {
    private final ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    
    public ModificarEstadoConfiguracionAlarmaUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    //En caso de que se vaya a habilitar o inhabilitar una configuración {nuevoEstado: false para inhabilitar, true para habilitar}
    public OperationResult<ConfiguracionAlarmaDTO> execute(String email, String nivel_notificacion, boolean nuevoEstado) {
        try {
            ConfiguracionAlarma config = configuracionAlarmaRepository.obtenerConfiguracionAlarma(email);
            config.setActivo(nuevoEstado);
            ConfiguracionAlarma resultConfig = configuracionAlarmaRepository.guardar(config);
            if (resultConfig == null) {
                return OperationResult.fail("Error actualizando el correo");
            }
            ConfiguracionAlarmaDTO configdto = new ConfiguracionAlarmaDTO(config);
            return OperationResult.ok("Correo actualizado correctamente", configdto);
        } catch (Exception e) {
            return OperationResult.fail("Error al actualizar el correo" + e.getMessage());
        }
    }
}
