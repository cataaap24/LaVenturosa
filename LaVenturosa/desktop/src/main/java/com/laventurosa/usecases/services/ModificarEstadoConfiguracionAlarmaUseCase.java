package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

public class ModificarEstadoConfiguracionAlarmaUseCase {
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    
    public ModificarEstadoConfiguracionAlarmaUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    //En caso de que se vaya a habilitar o inhabilitar una configuración {nuevoEstado: false para inhabilitar, true para habilitar}
    public OperationResult<ConfiguracionAlarma> execute(String email, String nivel_notificacion, boolean nuevoEstado) {
        try {
            ConfiguracionAlarma config = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.valueOf(nivel_notificacion));
            config.setActivo(nuevoEstado);
            ConfiguracionAlarma resultConfig = configuracionAlarmaRepository.guardar(config);
            if (resultConfig != null) {
                return OperationResult.ok("Correo actualizado correctamente");
            }
            return OperationResult.fail("Error actualizando el correo");
        } catch (Exception e) {
            return OperationResult.fail("Error al actualizar el correo" + e.getMessage());
        }
    }
}
