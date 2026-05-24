package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

public class ConfigurarAlarmaUseCase {
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;

    public ConfigurarAlarmaUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    //En caso de que se vaya a agregar una nueva configuración
    public OperationResult execute(String email, String nivel_notificacion) {
        if (email.isBlank() || nivel_notificacion.isBlank()) {
            return OperationResult.fail("Campos vacíos");
        }
        try {
            ConfiguracionAlarma config = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.valueOf(nivel_notificacion));
            ConfiguracionAlarma resultConfig = configuracionAlarmaRepository.guardar(config);
            if (resultConfig != null) {
                return OperationResult.ok("Correo agregado correctamente");
            }
            return OperationResult.fail("Error al guardar el correo");
        } catch (Exception e) {
            return OperationResult.fail("Error al guardar el correo: " + e.getMessage());
        }
    }

    //En caso de que se vaya a habilitar o inhabilitar una configuración {nuevoEstado: false para inhabilitar, true para habilitar}
    public OperationResult execute(String email, String nivel_notificacion, boolean nuevoEstado) {
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
