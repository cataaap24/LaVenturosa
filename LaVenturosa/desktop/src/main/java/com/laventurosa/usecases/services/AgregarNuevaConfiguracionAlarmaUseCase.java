package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.ConfiguracionAlarmaDTO;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.util.regex.Pattern;

public class AgregarNuevaConfiguracionAlarmaUseCase {
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    public AgregarNuevaConfiguracionAlarmaUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    //En caso de que se vaya a agregar una nueva configuración
    public OperationResult<ConfiguracionAlarmaDTO> execute(String email, String nivel_notificacion) {
        if (email == null || email.isBlank() || nivel_notificacion.isBlank()) {
            return OperationResult.fail("Campos vacíos");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return OperationResult.fail("El email no tiene un formato válido.");
        }
        //Validar que el correo no este ya registrado
        if (configuracionAlarmaRepository.obtenerConfiguracionAlarma(email) != null) return OperationResult.fail("Error: este correo ya existe");
        try {
            ConfiguracionAlarma config = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.valueOf(nivel_notificacion));
            ConfiguracionAlarma resultConfig = configuracionAlarmaRepository.guardar(config);
            ConfiguracionAlarmaDTO data = new ConfiguracionAlarmaDTO(resultConfig);
            if (resultConfig != null) {
                return OperationResult.ok("Correo agregado correctamente", data);
            }
            return OperationResult.fail("Error al guardar el correo");
        } catch (Exception e) {
            return OperationResult.fail("Error al guardar el correo: " + e.getMessage());
        }
    }
}
